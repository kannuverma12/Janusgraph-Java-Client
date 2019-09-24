package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.cache.redis.RedisCacheService;
import com.paytm.digital.education.coaching.consumer.model.dto.CartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.ConvTaxInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.MetaData;
import com.paytm.digital.education.coaching.consumer.model.dto.TCS;
import com.paytm.digital.education.coaching.consumer.model.dto.TaxInfo;
import com.paytm.digital.education.coaching.consumer.model.request.MerchantProduct;
import com.paytm.digital.education.coaching.consumer.model.request.PostMerchantProductsRequest;
import com.paytm.digital.education.coaching.consumer.model.response.CartDataResponse;
import com.paytm.digital.education.database.entity.ItemEntity;
import com.paytm.digital.education.database.repository.ItemRepository;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_MERCHANT_ID;

@Slf4j
@Service
public class MerchantProductsTransformerService {

    // To Do , correct ID to be inserted once we get it from product
    private static final String EDUCATION_VERTICAL_ID = "0";
    private static final String EDUCATION_CATEGORY_ID = "0";
    private static final String CACHE_KEY_DELIMITER   = "_";
    private static final int    DEFAULT_QUANTITY      = 1;
    private static final int    CACHE_TTL             = 1800;


    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private ItemRepository itemRepository;

    public CartDataResponse getCartDataFromVertical(PostMerchantProductsRequest request) {

        log.info("Got merchant product data request {}", request);
        List<ItemEntity> itemEntityList = itemRepository.findByMerchantId(request.getMerchantId());
        if (CollectionUtils.isEmpty(itemEntityList)) {
            log.error("Item with Merchant id: {} does not exist", request.getMerchantId());
            throw new BadRequestException(INVALID_MERCHANT_ID);
        }
        ItemEntity itemEntity = itemEntityList.get(0);
        List<CartItem> cartItemList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(request.getMerchantProductList())) {
            for (MerchantProduct merchantProduct : request.getMerchantProductList()) {
                CartItem cartItem = CartItem.builder()
                        .sellingPrice(merchantProduct.getPrice())
                        .productId(itemEntity.getPaytmProductId())
                        .categoryId(EDUCATION_CATEGORY_ID)
                        .educationVertical(EDUCATION_VERTICAL_ID)
                        .qty(DEFAULT_QUANTITY)
                        .metaData(getMetaData(merchantProduct, request))
                        .referenceId("")
                        .basePrice(merchantProduct.getPrice())
                        .convFee(0F)
                        .build();
                cartItemList.add(cartItem);
                String cacheKey = getCacheKey(request, itemEntity, merchantProduct);
                String cacheValue = JsonUtils.toJson(cartItem);
                redisCacheService.addKeyToCache(cacheKey, cacheValue, CACHE_TTL);
            }
        }
        return CartDataResponse.builder().cartItems(cartItemList).build();
    }

    private String getCacheKey(PostMerchantProductsRequest request, ItemEntity itemEntity,
            MerchantProduct merchantProduct) {
        return String.valueOf(request.getUserId())
                + CACHE_KEY_DELIMITER + String.valueOf(merchantProduct.getProductId())
                + CACHE_KEY_DELIMITER + String.valueOf(itemEntity.getPaytmProductId());
    }

    private MetaData getMetaData(MerchantProduct merchantProduct,
            PostMerchantProductsRequest request) {
        TCS tcs = TCS
                .builder()
                .basePrice(0F)
                .igst(0F)
                .cgst(0F)
                .sgst(0F)
                .utgst(0F)
                .spin(0L)
                .dpin(0L)
                .sac(0L)
                .agstin(null)
                .hsn(null)
                .cpin(null)
                .cgstin(null)
                .build();

        TaxInfo taxInfo = null;
        if (Objects.nonNull(merchantProduct.getMerchantProductTaxData())) {
            taxInfo = TaxInfo.builder()
                    .gstin(Objects.nonNull(merchantProduct.getMerchantProductTaxData().getGstin())
                            ? merchantProduct.getMerchantProductTaxData().getGstin() :
                            StringUtils.EMPTY)
                    .totalCGST(Objects.nonNull(
                            merchantProduct.getMerchantProductTaxData().getTotalCGST())
                            ? merchantProduct.getMerchantProductTaxData().getTotalCGST() : 0F)
                    .totalIGST(Objects.nonNull(
                            merchantProduct.getMerchantProductTaxData().getTotalIGST())
                            ? merchantProduct.getMerchantProductTaxData().getTotalIGST() : 0F)
                    .totalSGST(Objects.nonNull(
                            merchantProduct.getMerchantProductTaxData().getTotalSGST())
                            ? merchantProduct.getMerchantProductTaxData().getTotalSGST() : 0F)
                    .totalUTGST(Objects.nonNull(
                            merchantProduct.getMerchantProductTaxData().getTotalUTGST())
                            ? merchantProduct.getMerchantProductTaxData().getTotalUTGST() : 0F)
                    .build();
        } else {
            taxInfo = TaxInfo.builder()
                    .gstin(StringUtils.EMPTY)
                    .totalCGST(0F)
                    .totalIGST(0F)
                    .totalSGST(0F)
                    .totalUTGST(0F)
                    .build();
        }

        ConvTaxInfo convTaxInfo = ConvTaxInfo.builder()
                .gstin(null)
                .totalCGST(0F)
                .totalIGST(0F)
                .totalSGST(0F)
                .totalUTGST(0F)
                .build();

        return MetaData
                .builder()
                .convTaxInfo(convTaxInfo)
                .taxInfo(taxInfo)
                .tcs(tcs)
                .userId(request.getUserId())
                .merchantProductId(merchantProduct.getProductId())
                .build();
    }

}
