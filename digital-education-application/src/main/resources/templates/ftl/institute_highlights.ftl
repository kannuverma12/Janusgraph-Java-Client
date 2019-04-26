<#function maximum a b>
  <#if (a > b)>
    <#return a>
  <#else>
    <#return b>
   </#if>
</#function>
<#if institute??>
  {"highlights":[
  <#if institute.accreditations??>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"ACCREDITATIONS","description":"${institute.accreditations[0].name?capitalize}"},
  </#if>
  <#if institute.salariesPlacement??>
    <#if institute.salariesPlacement[0].maximum??>
      <#assign val = institute.salariesPlacement[0].maximum>
      <#list institute.salariesPlacement as salary>
        <#if salary.maximum??>
          <#assign val = maximum(val, salary.maximum)>
        </#if>
      </#list>
      <#assign val = val/100000>
      {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"MAX PACKAGE","description":"${val} Lac"}
    <#elseif institute.salariesPlacement[0].median??>
      <#assign val = institute.salariesPlacement[0].median>
      <#list institute.salariesPlacement as salary>
        <#if salary.median??>
          <#assign val = maximum(val, salary.median)>
        </#if>
      </#list>
      <#assign val = val/100000>
      {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"MEDIAN PACKAGE","description":"${val} Lac"}
    <#elseif institute.salariesPlacement[0].average??>
      <#assign val = institute.salariesPlacement[0].average>
      <#list institute.salariesPlacement as salary>
        <#if salary.average??>
            <#assign val = maximum(val, salary.average)>
        </#if>
      </#list>
      <#assign val = val/100000>
      {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"AVERAGE PACKAGE","description":"${val} Lac"}
    </#if>,
  </#if>
  <#if institute.studentCount?? && institute.studentCount &gt; 0>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"NO OF STUDENTS","description":"${institute.studentCount}"},
  </#if>
  <#if institute.facultyCount?? && institute.facultyCount &gt; 0>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"FACULTIES","description":"${institute.facultyCount}"},
  </#if>
  <#if approvals??>
    <#list approvals?keys as approvalKey>
        {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"${approvalKey?upper_case}","description":"${approvals[approvalKey]}"},
    </#list>
  </#if>
  <#if institute.instituteTypes??>
    <#assign instituteType="${institute.instituteTypes[0]?replace('[-_]*', ' ')}">
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"INSTITUTE TYPE","description":"${instituteType?capitalize}"},
  </#if>
  <#if institute.ownership??>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"OWNERSHIP","description":"${institute.ownership?capitalize}"},
  </#if>
  <#if institute.gendersAccepted??>
    <#assign genders= "">
    <#if institute.gendersAccepted?size == 2>
      <#assign genders="Co-ed">
    <#else>
      <#assign genders="${institute.gendersAccepted[0]?capitalize}">
    </#if>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"GENDER ACCEPTED","description":"${genders}"},
  </#if>
  <#if institute.totalEnrollments?? && institute.totalEnrollments &gt; 0>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"TOTAL STUDENT ENROLLMENT","description":"${institute.totalEnrollments}"},
  </#if>
  <#if institute.campusSize??>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"CAMPUS SIZE","description":"${institute.campusSize} Acres"},
  </#if>
  <#if institute.establishedYear??>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"ESTABLISHED YEAR","description":"Year ${institute.establishedYear?c}"}
  <#else>{}</#if>
  ]}
<#else>
  {}
</#if>
