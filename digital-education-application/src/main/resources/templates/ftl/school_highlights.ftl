<#if school??>
    {"highlights":[
    <#if school.establishedYear??>
        {"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/establishment_year.svg","title":"Established Year","description":"${school.establishedYear}"},
    </#if>
    <#if school.schoolSize??>
        {"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/campus_size.svg","title":"School Size","description":"${school.schoolSize}"},
    </#if>
    <#if school.boardList?has_content>
        <#assign boardData=school.boardList[0].data>
    </#if>
    <#if boardData??>
        <#if boardData.gender?has_content>
            {"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/co_ed.svg","title":"Gender","description":"${boardData.gender}"},
        </#if>
        <#if boardData.noOfClassrooms?has_content>
            {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"No. of Classrooms","description":"${boardData.noOfClassrooms}"},
        </#if>
        <#if boardData.enrollments?has_content>
            {"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/total_student.svg","title":"No. of Students","description":"${boardData.enrollments}"},
        </#if>
        <#if boardData.mediumOfInstruction?has_content>
            {"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/medium.svg","title":"Medium","description":"${boardData.mediumOfInstruction?join(", ")}"}
        </#if>
    </#if>
    ]}
<#else>
    {}
</#if>
