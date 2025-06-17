package ninegle.Readio.user.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/** ///////////////////////////////// */ //권한을 가진 admin만 통과가 되는지 테스트
@RestController
class TestController {
    @GetMapping("/admin/test")
    fun test(): String {
        return "test"
    }

    // USER 권한을 가지고 통과가 되는지
    @GetMapping("/test")
    fun test2(): String {
        return "user"
    }
}