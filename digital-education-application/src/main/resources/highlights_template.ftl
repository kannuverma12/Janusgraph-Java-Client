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
    <#assign i = 0>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Accreditations","description":"${institute.accreditations[i].name}"},
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
      {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Max Package","description":"${val} Lac"}
    <#elseif institute.salariesPlacement[0].median??>
      <#assign val = institute.salariesPlacement[0].median>
      <#list institute.salariesPlacement as salary>
        <#if salary.median??>
          <#assign val = maximum(val, salary.median)>
        </#if>
      </#list>
      <#assign val = val/100000>
      {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Median Package","description":"${val} Lac"}
    <#elseif institute.salariesPlacement[0].average??>
      <#assign val = institute.salariesPlacement[0].average>
      <#list institute.salariesPlacement as salary>
        <#if salary.average??>
            <#assign val = maximum(val, salary.average)>
        </#if>
      </#list>
      <#assign val = val/100000>
      {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Average Package","description":"${val} Lac"}
    </#if>,
  </#if>
  <#if institute.studentCount??>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"No of Students","description":"${institute.studentCount}"},
  </#if>
  <#if institute.facultyCount??>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Faculties","description":"${institute.facultyCount}"},
  </#if>
  <#if institute.approvals??>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Approved By","description":"${institute.approvals?join(", ")}"},
  </#if>
  <#if institute.instituteTypes??>
    <#assign i = 0>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Institute Type","description":"${institute.instituteTypes[i]}"},
  </#if>
  <#if institute.ownership??>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Ownership","description":"${institute.ownership}"},
  </#if>
  <#if institute.establishedYear??>
    {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Established Year","description":"Year ${institute.establishedYear?c}"}
  <#else>{}</#if>
  ]}
<#else>
  {}
</#if>
