package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.explore.enums.EducationEntity.INSTITUTE;

import com.paytm.digital.education.explore.response.dto.detail.InstituteComparison;
import com.paytm.digital.education.explore.response.dto.detail.InstituteDetail;
import com.paytm.digital.education.explore.service.CompareService;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompareServiceImpl implements CompareService {

  @Autowired
  private InstituteDetailServiceImpl instituteDetailService;

  @Override
  public InstituteComparison compareInstitutes(Long institute1, Long institute2, String fieldGroup,
      List<String> fields) throws IOException, TimeoutException {

    InstituteDetail institute1Detail = instituteDetailService.getinstituteDetail(institute1, fieldGroup);

    InstituteDetail institute2Detail = instituteDetailService.getinstituteDetail(institute2, fieldGroup);

    List<Map<Long, InstituteDetail>> instituteList = new ArrayList<> ();

    Map<Long, InstituteDetail> map = new HashMap<> ( );
    map.put ( institute1Detail.getInstituteId (), institute1Detail );
    map.put ( institute2Detail.getInstituteId (), institute2Detail );
    instituteList.add ( map );
    InstituteComparison instituteComparison = new InstituteComparison ();
    instituteComparison.setInstituteList ( instituteList );
    return instituteComparison;
  }
}
