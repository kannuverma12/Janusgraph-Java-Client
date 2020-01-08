package com.paytm.digital.education.explore.service.impl;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.paytm.digital.education.advice.helper.KeyGenerator;
import com.paytm.digital.education.annotation.EduCache;
import com.paytm.digital.education.database.entity.CustomerAction;
import com.paytm.digital.education.database.entity.InstituteByProduct;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.Action;
import com.paytm.digital.education.enums.Product;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.exception.NotFoundException;
import com.paytm.digital.education.explore.dto.ActionCountReportDto;
import com.paytm.digital.education.explore.dto.ActionFullReportDto;
import com.paytm.digital.education.explore.dto.InstituteByProductDto;
import com.paytm.digital.education.explore.dto.InstituteListResponseDto;
import com.paytm.digital.education.explore.service.InstituteByProductService;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.output.StringBuilderWriter;
import org.bson.Document;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.List;

import static com.opencsv.CSVWriter.DEFAULT_SEPARATOR;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.constant.InstituteByProductConstants.ACTION;
import static com.paytm.digital.education.constant.InstituteByProductConstants.CUSTOMER_ACTION;
import static com.paytm.digital.education.constant.InstituteByProductConstants.EMAIL;
import static com.paytm.digital.education.constant.InstituteByProductConstants.INSTITUTE_BY_PRODUCT_ENTRY_ID;
import static com.paytm.digital.education.constant.InstituteByProductConstants.IS_DELETED;
import static com.paytm.digital.education.enums.Action.SHOW_INTEREST;
import static com.paytm.digital.education.mapping.ErrorEnum.DUPLICATE_INSTITUTE_ERROR;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_ENTITY_FOUND;
import static com.paytm.digital.education.mapping.ErrorEnum.REPORT_GENERATION_ERROR;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@RequiredArgsConstructor
public class InstituteByProductServiceImpl implements InstituteByProductService {
    private static final Document ADD_FIELD_QUERY_DOCUMENT = new Document("$addFields",
            new Document("has_shown_interest",
                    new Document("$not", new Document("$not", "$customer_action_data._id"))));

    private static final AggregationOperation ADD_FIELD_AGGREGATION = aoc -> ADD_FIELD_QUERY_DOCUMENT;

    private static final Logger log = LoggerFactory.getLogger(InstituteByProductServiceImpl.class);

    private static final String ACTION_DUPLICATE_TEMPLATE =
            "Action {} has already been performed by user {} on institute {}";

    private static final String PRODUCT = "product";

    private static final String EXPLORE_INSTITUTE_ID = "explore_institute_id";

    private static final String INSTITUTE_BY_PRODUCT = "institute_by_product";

    private static final String CUSTOMER_ACTION_DATA = "customer_action_data";

    private static final String ID = "_id";

    private static final String DATA = "data";

    private static final String CSV_GENERATION_ERROR = "Error while generating CSV";

    private static final String ORDER = "order";
    private static final String COUNT = "count";

    private static final String[] KEYS = {};
    private static final String CACHE = "instituteByProduct";
    private static final boolean SHOULD_CACHE_NULL = false;

    private static final EduCache EDU_CACHE = new EduCache() {

        @Override
        public Class<? extends Annotation> annotationType() {
            return null;
        }

        @Override
        public String[] keys() {
            return KEYS;
        }

        @Override
        public String cache() {
            return CACHE;
        }

        @Override
        public boolean shouldCacheNull() {
            return SHOULD_CACHE_NULL;
        }
    };

    private final MongoOperations mongoOperation;
    private final CommonMongoRepository commonMongoRepository;
    private final EmailService emailService;
    private final KeyGenerator keyGenerator;
    private final StringRedisTemplate redisTemplate;

    @Override
    @EduCache(cache = CACHE, shouldCacheNull = SHOULD_CACHE_NULL)
    public InstituteListResponseDto getInstitutesByProduct(Long userId, Product product, String email) {
        Aggregation agg = newAggregation(
                match(where(PRODUCT).is(product)),
                sort(ASC, ORDER),
                getLookupQueryDocument(email),
                unwind(CUSTOMER_ACTION_DATA, true),
                ADD_FIELD_AGGREGATION);

        List<InstituteByProductDto> instituteByProductDtoList = mongoOperation
                .aggregate(agg, INSTITUTE_BY_PRODUCT, InstituteByProductDto.class).getMappedResults();
        return InstituteListResponseDto.builder()
                .isEmailPresent(isNotBlank(email))
                .instituteByProducts(instituteByProductDtoList)
                .build();
    }

    @Override
    public CustomerAction postCustomerActionForProduct(
            Long userId, Product product, String email, Long exploreInstituteId, Action action) {
        final String key = keyGenerator.generateKey(
                EDU_CACHE,
                InstituteByProductServiceImpl.class,
                "getInstitutesByProduct",
                new String[]{"userId", "product", "email"},
                new Object[]{userId, product, email}
        );
        redisTemplate.delete(key);
        InstituteByProduct instituteByProduct = getInstituteByProduct(exploreInstituteId, product);
        CustomerAction customerAction =
                mongoOperation.findOne(
                        query(where(INSTITUTE_BY_PRODUCT_ENTRY_ID).is(instituteByProduct.getId())
                                .and(EMAIL).is(email)
                                .and(ACTION).is(action)
                                .and(IS_DELETED).is(true)), CustomerAction.class);
        if (customerAction == null) {
            customerAction = new CustomerAction(instituteByProduct.getId(), email, action);
        } else {
            customerAction.setIsDeleted(null);
            customerAction.setUpdatedAt(new Date());
        }
        try {
            commonMongoRepository.saveOrUpdate(customerAction);
        } catch (DuplicateKeyException duplicateKeyException) {
            log.warn(ACTION_DUPLICATE_TEMPLATE, action, email, exploreInstituteId);
        }
        return customerAction;
    }

    @Override
    public CustomerAction deleteCustomerActionForProduct(
            Long userId, Product product, String email, Long exploreInstituteId, Action action) {
        InstituteByProduct instituteByProduct = getInstituteByProduct(exploreInstituteId, product);
        CustomerAction customerAction =
                mongoOperation.findOne(
                        query(where(INSTITUTE_BY_PRODUCT_ENTRY_ID).is(instituteByProduct.getId())
                                .and(EMAIL).is(email)
                                .and(ACTION).is(action)
                                .and(IS_DELETED).is(null)), CustomerAction.class);
        if (customerAction == null) {
            throw new NotFoundException(
                    NO_ENTITY_FOUND,
                    NO_ENTITY_FOUND.getExternalMessage(),
                    new Object[]{CUSTOMER_ACTION, EMAIL, email}
            );
        }
        customerAction.setIsDeleted(true);
        commonMongoRepository.saveOrUpdate(customerAction);
        return customerAction;
    }

    private String computeReport() {
        String report1 = computeFullReport();
        String report2 = computeCountReport();
        return report1 + report2;

    }

    private String computeCountReport() {
        Aggregation agg = newAggregation(
                match(where(IS_DELETED).is(null).and(ACTION).is(SHOW_INTEREST)),
                group(INSTITUTE_BY_PRODUCT_ENTRY_ID).count().as(COUNT),
                sort(DESC, COUNT),
                lookup(INSTITUTE_BY_PRODUCT, ID, ID, DATA),
                unwind(DATA),
                project()
                        .and(DATA + "." + EXPLORE_INSTITUTE_ID).as(EXPLORE_INSTITUTE_ID)
                        .and(COUNT).as(COUNT)
                        .and(DATA + "." + PRODUCT).as(PRODUCT)
        );
        List<ActionCountReportDto> actionReportDtos =
                mongoOperation.aggregate(agg, CUSTOMER_ACTION, ActionCountReportDto.class).getMappedResults();
        try {
            StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
            StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(stringBuilderWriter)
                    .withQuotechar('\0')
                    .withSeparator(DEFAULT_SEPARATOR)
                    .build();

            sbc.write(actionReportDtos);
            stringBuilderWriter.flush();
            stringBuilderWriter.close();
            return stringBuilderWriter.toString();
        } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            log.error(CSV_GENERATION_ERROR);
        }
        return null;
    }

    private String computeFullReport() {
        Aggregation agg = newAggregation(
                match(where(IS_DELETED).is(null).and(ACTION).is(SHOW_INTEREST)),
                group(INSTITUTE_BY_PRODUCT_ENTRY_ID).count().as(COUNT),
                sort(DESC, COUNT),
                lookup(CUSTOMER_ACTION, ID, INSTITUTE_BY_PRODUCT_ENTRY_ID, DATA),
                unwind(DATA),
                replaceRoot(DATA),
                lookup(INSTITUTE_BY_PRODUCT, INSTITUTE_BY_PRODUCT_ENTRY_ID,
                        ID, DATA),
                unwind(DATA),
                project().and(EMAIL).as(EMAIL)
                        .and(ACTION).as(ACTION)
                        .and(DATA + "." + EXPLORE_INSTITUTE_ID).as(EXPLORE_INSTITUTE_ID)
                        .and(DATA + "." + PRODUCT).as(PRODUCT)
        );
        List<ActionFullReportDto> actionReportDtos =
                mongoOperation.aggregate(agg, CUSTOMER_ACTION, ActionFullReportDto.class).getMappedResults();
        try {
            StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
            StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(stringBuilderWriter)
                    .withQuotechar('\0')
                    .withSeparator(DEFAULT_SEPARATOR)
                    .build();

            sbc.write(actionReportDtos);
            stringBuilderWriter.flush();
            stringBuilderWriter.close();
            return stringBuilderWriter.toString();
        } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            log.error(CSV_GENERATION_ERROR);
        }
        return null;
    }

    @Override
    public void sendReport() {
        String report = computeReport();
        emailService.sendMailWithAttachment("test@gmail.com", "report", "val", report);
    }

    @Override
    public InstituteByProduct saveInstituteByProduct(InstituteByProduct instituteByProduct) {
        try {
            commonMongoRepository.saveOrUpdate(instituteByProduct);
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new EducationException(
                    DUPLICATE_INSTITUTE_ERROR,
                    DUPLICATE_INSTITUTE_ERROR.getExternalMessage(),
                    new Object[]{instituteByProduct.getExploreInstituteId(), instituteByProduct.getProduct()}
            );
        }
        return instituteByProduct;
    }

    @Override
    public void getReport(HttpServletResponse response) {
        String report = computeReport();
        if (report == null) {
            throw new EducationException(
                    REPORT_GENERATION_ERROR,
                    REPORT_GENERATION_ERROR.getExternalMessage()
            );
        }
        try {
            Writer writer = response.getWriter();
            writer.write(report);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AggregationOperation getLookupQueryDocument(String email) {
        return aoc -> new Document("$lookup",
                new Document("from", CUSTOMER_ACTION)
                        .append("as", CUSTOMER_ACTION_DATA)
                        .append("let", new Document(INSTITUTE_ID, "$" + ID))
                        .append("pipeline", asList(
                                new Document("$match", new Document("$and", asList(
                                        new Document("action", SHOW_INTEREST.name())
                                                .append("email", email)
                                                .append("is_deleted", null),
                                        new Document("$expr", new Document("$eq",
                                                asList("$" + INSTITUTE_BY_PRODUCT_ENTRY_ID, "$$" + INSTITUTE_ID)))
                                ))),
                                new Document("$project", new Document("_id", 1)))
                        )
        );
    }

    private InstituteByProduct getInstituteByProduct(Long exploreInstituteId, Product product) {
        InstituteByProduct instituteByProduct =
                mongoOperation.findOne(query(where(EXPLORE_INSTITUTE_ID).is(exploreInstituteId)
                        .and(PRODUCT).is(product)), InstituteByProduct.class);
        if (instituteByProduct == null) {
            throw new NotFoundException(
                    NO_ENTITY_FOUND,
                    NO_ENTITY_FOUND.getExternalMessage(),
                    new Object[]{INSTITUTE_BY_PRODUCT, EXPLORE_INSTITUTE_ID, exploreInstituteId}
            );
        }
        return instituteByProduct;
    }
}
