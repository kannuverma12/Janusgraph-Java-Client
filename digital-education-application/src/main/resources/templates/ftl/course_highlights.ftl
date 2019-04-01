<#if course?? || exam??>
  {"highlights":[
  <#if course?? && course.courseFees??>
    <#list course.courseFees as fee>
       <#if !fee.casteGroup?? || fee.casteGroup == "GENERAL">
         {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Total Fees","description":"${fee.fee}"},
         <#break>
       </#if>
    </#list>
  </#if>
  <#if exam??>
    <#if exam.examShortName??>
      {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Exams Accepted","description":"${exam.examShortName}"},
    <#else>
      {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Exams Accepted","description":"${exam.examFullName}"},
    </#if>
  </#if>
  <#if course?? && course.seatsAvailable??>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Seats Available","description":"${course.seatsAvailable}"},
  </#if>
  <#if course?? && course.courseLevel??>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Course Level","description":"${course.courseLevel}"},
  </#if>
  <#if course?? && course.masterBranch??>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Master Branch","description":"${course.masterBranch}"}
  <#else>{}</#if>
  ]}
<#else>
  {}
</#if>
