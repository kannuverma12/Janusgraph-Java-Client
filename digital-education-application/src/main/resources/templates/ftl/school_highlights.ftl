<#if school??>
    <#if client?has_content>
        <#assign clientName="${client.getName()}" >
    <#else>
        <#assign clientName="web" >
    </#if>
    <#assign highlights=[]>
    <#if school.establishedYear??>
        <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/${clientName}/establishment_year.svg","title":"Established Year","description":"${school.establishedYear?c}"}']>
    </#if>
    <#if school.schoolSize??>
        <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/${clientName}/campus_size.svg","title":"School Size","description":"${school.schoolSize?c} ${school.schoolSizeUnit!""}"}']>
    </#if>
    <#if school.boardList?has_content>
        <#assign boardData=school.boardList[0].data>
    </#if>
    <#if boardData??>
        <#assign genderString = boardData.gender?has_content?then(boardData.gender.getReadableValue(), "")>
        <#if genderString?has_content>
            <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/${clientName}/co_ed.svg","title":"Gender","description":"${genderString}"}']>
        </#if>
        <#if boardData.noOfClassrooms?has_content>
            <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/${clientName}/no_of_classrooms.svg","title":"No. of Classrooms","description":"${boardData.noOfClassrooms}"}']>
        </#if>
        <#assign enrollments=boardData.enrollments()>
        <#if enrollments?has_content && enrollments != 0>
            <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/${clientName}/total_student.svg","title":"No. of Students","description":"${enrollments}"}']>
        </#if>
        <#if boardData.mediumOfInstruction?has_content>
            <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/${clientName}/medium.svg","title":"Medium","description":"${boardData.mediumOfInstruction?join(", ")}"}']>
        </#if>
        <#if boardData.disableStudentFriendly?has_content>
            <#if boardData.disableStudentFriendly == 1>
                <#assign disableStudentFriendlyHumanReadable = "YES">
            <#else>
                <#assign disableStudentFriendlyHumanReadable = "NO">
            </#if>
            <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/${clientName}/disable_student_friendly.svg","title":"Disable Student Friendly","description":"${disableStudentFriendlyHumanReadable}"}']>
        </#if>
        <#assign ownershipString = boardData.ownership?has_content?then(boardData.ownership.getReadableValue(), "")>
        <#if ownershipString?has_content>
            <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/${clientName}/ownership.svg","title":"Ownership","description":"${ownershipString}"}']>
        </#if>
        <#if boardData.residentialStatus?has_content>
            <#assign highlights = highlights + ['{"logo_url":"https://assetscdn1.paytm.com/educationwebassets/education/explore/highlight/${clientName}/residential_status.svg","title":"Residential Status","description":"${boardData.residentialStatus?join(", ")}"}']>
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
