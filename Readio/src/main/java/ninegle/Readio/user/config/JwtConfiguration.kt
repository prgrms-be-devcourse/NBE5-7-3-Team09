package ninegle.Readio.user.config

import org.springframework.boot.context.properties.ConfigurationProperties

//메인에서도 @ConfigurationPropertiesScan 이걸 해줘야 properties 파일들을 스캔해서 가져온다
//exp에 값을 먼저 넣고 Validation을 생성 후 그걸 가지고 validation 생성


@ConfigurationProperties(prefix = "custom.jwt")
data class JwtConfiguration  (
     var secrets: Secrets,
     var expTime: ExpTime)
{
    data class Secrets (
        var originkey: String,
        var appkey: String)


    data class ExpTime (
        var access: Long = 0,
        var refresh: Long = 0
            )
}
