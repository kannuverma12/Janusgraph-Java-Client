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
    {"logo_url":"${highlight_base_url}/constituent.svg","title":"Accreditations","description":"${institute.accreditations[0].name?upper_case}"},
  </#if>
  <#if institute.salariesPlacement??>
    <#if institute.salariesPlacement[0].maximum??>
      <#assign val = institute.salariesPlacement[0].maximum>
      <#list institute.salariesPlacement as salary>
        <#if salary.maximum??> <#assign val = maximum(val, salary.maximum)> </#if>
      </#list>
      <#assign val = val/100000>
      <#if val &gt; 1 >
        {"logo_url":"${highlight_base_url}/salary_package.svg","title":"Max Package","description":"${val} Lakhs"}
      <#else>
        {"logo_url":"${highlight_base_url}/salary_package.svg","title":"Max Package","description":"${val} Lakh"}
      </#if>
    <#elseif institute.salariesPlacement[0].median??>
      <#assign val = institute.salariesPlacement[0].median>
      <#list institute.salariesPlacement as salary>
        <#if salary.median??><#assign val = maximum(val, salary.median)> </#if>
      </#list>
      <#assign val = val/100000>
      <#if val &gt; 1 >
        {"logo_url":"${highlight_base_url}/salary_package.svg","title":"Median Package","description":"${val} Lakhs"}
      <#else>
        {"logo_url":"${highlight_base_url}/salary_package.svg","title":"Median Package","description":"${val} Lakh"}
      </#if>
    <#elseif institute.salariesPlacement[0].average??>
      <#assign val = institute.salariesPlacement[0].average>
      <#list institute.salariesPlacement as salary>
        <#if salary.average??>
          <#assign val = maximum(val, salary.average)>
        </#if>
      </#list>
      <#assign val = val/100000>
      <#if val &gt; 1 >
        {"logo_url":"${highlight_base_url}/salary_package.svg","title":"Average Package","description":"${val} Lakhs"}
      <#else>
        {"logo_url":"${highlight_base_url}/salary_package.svg","title":"Average Package","description":"${val} Lakh"}
      </#if>
    </#if>,
  </#if>
  <#if institute.studentCount?? && institute.studentCount &gt; 0 >
    {"logo_url":"${highlight_base_url}/total_student.svg","title":"Student Intake","description":"${institute.studentCount}"},
  </#if>
  <#if institute.facultyCount?? && institute.facultyCount &gt; 0 >
    {"logo_url":"${highlight_base_url}/total_faculty.svg","title":"Faculties","description":"${institute.facultyCount}"},
  </#if>
  <#if approvals??>
    <#list approvals?keys as approvalKey>
      {"logo_url":"${highlight_base_url}/affiliated_to_ugc.svg","title":"${approvalKey}","description":"${approvals[approvalKey]}"},
    </#list>
  </#if>
  <#if institute.instituteTypes??>
    <#assign instituteType="${institute.instituteTypes[0]?replace('_', ' ')}">
    {"logo_url":"${highlight_base_url}/establishment_year.svg","title":"Institute Type","description":"${instituteType?capitalize}"},
  </#if>
  <#if institute.ownership??>
    {"logo_url":"${highlight_base_url}/ownership.svg","title":"Ownership","description":"${institute.ownership?capitalize}"},
  </#if>
  <#if institute.gendersAccepted??>
    <#assign genders= "">
    <#if institute.gendersAccepted?size == 2>
      <#assign genders="Co-ed">
    <#else>
      <#assign genders="${institute.gendersAccepted[0]?capitalize}">
    </#if>
    {"logo_url":"${highlight_base_url}/co_ed.svg","title":"Gender Accepted","description":"${genders}"},
  </#if>
  <#if institute.totalEnrollments?? && institute.totalEnrollments &gt; 0 >
    {"logo_url":"${highlight_base_url}/total_student.svg","title":"Number of Students","description":"${institute.totalEnrollments}"},
  </#if>
  <#if institute.campusSize??>
    {"logo_url":"${highlight_base_url}/campus_size.svg","title":"Campus size","description":"${institute.campusSize} Acres"},
  </#if>
  <#if institute.establishedYear??>
    {"logo_url":"${highlight_base_url}/establishment_year.svg","title":"Established Year","description":"Year ${institute.establishedYear?c}"}
  <#else>
    {}
  </#if>
  ]}
<#else>
  {}
</#if>
