<#if school??>
    <#assign highlights=[]>
    <#if school.establishedYear??>
        <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/establishment_year.svg","title":"Established Year","description":"${school.establishedYear?c}"}']>
    </#if>
    <#if school.schoolSize??>
        <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/campus_size.svg","title":"School Size","description":"${school.schoolSize?c} ${school.schoolSizeUnit!""}"}']>
    </#if>
    <#if school.boardList?has_content>
        <#assign boardData=school.boardList[0].data>
    </#if>
    <#if boardData??>
        <#if boardData.gender?has_content>
            <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/co_ed.svg","title":"Gender","description":"${boardData.gender.getReadableValue()}"}']>
        </#if>
        <#if boardData.noOfClassrooms?has_content>
            <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/no_of_classrooms.svg","title":"No. of Classrooms","description":"${boardData.noOfClassrooms}"}']>
        </#if>
        <#if boardData.enrollments?has_content>
            <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/total_student.svg","title":"No. of Students","description":"${boardData.enrollments}"}']>
        </#if>
        <#if boardData.mediumOfInstruction?has_content>
            <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/medium.svg","title":"Medium","description":"${boardData.mediumOfInstruction?join(", ")}"}']>
        </#if>
        <#if boardData.disableStudentFriendly?has_content>
            <#if boardData.disableStudentFriendly == 1>
                <#assign disableStudentFriendlyHumanReadable = "YES">
            <#else>
                <#assign disableStudentFriendlyHumanReadable = "NO">
            </#if>
            <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/co_ed.svg","title":"Disable Student Friendly","description":"${disableStudentFriendlyHumanReadable}"}']>
        </#if>
        <#if boardData.ownership?has_content>
            <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/ownership.svg","title":"Ownership","description":"${boardData.ownership.getReadableValue()}"}']>
        </#if>
        <#if boardData.residentialStatus?has_content>
            <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/ownership.svg","title":"Residential Status","description":"${boardData.residentialStatus?join(", ")}"}']>
        </#if>
    </#if>
    {
       "highlights": [
            ${highlights?join(",")}
        ]
    }
<#else>
    {}
</#if>
