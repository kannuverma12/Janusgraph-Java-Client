<#function maximum a b>
    <#if (a > b)>
        <#return a>
    <#else>
        <#return b>
    </#if>
</#function>
<#if exam??>
{"highlights":[
    <#if exam.conductingBody??>
        {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Conducting Body","description":"${exam.conductingBody}"},
    </#if>
    <#if exam.examCategory??>
        {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Exam Category","description":"${exam.examCategory}"},
    </#if>
    <#if exam.levelOfExam??>
        {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Exam Level","description":"${exam.levelOfExam}"},
    </#if>
    <#if linguistic_medium??>
        {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Language","description":"${linguistic_medium?join(", ")}"},
    </#if>
    <#if exam.subExams??>
        <#if (exam.subExams?size == 1) && exam.subExams[0].durationHours??>
            {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Duration","description":"${exam.subExams[0].durationHours} Hours"},
        <#else >
            <#assign duration = 0>
            <#list exam.subExams as subexam>
                <#assign duration = maximum(subexam.durationHours, duration)>
            </#list>
            <#if duration != 0>
                {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Duration","description":"Up to ${duration} Hours"},
            </#if>
        </#if>
    </#if>
    <#if exam.frequencyOfConduct??>
        {"logo_url":"http://assetscdn1.paytm.com/educationwebassets/backend/Star.svg","title":"Frequency","description":"${exam.frequencyOfConduct} Times a year"}
    <#else>{}</#if>
]}
<#else>
{}
</#if>
