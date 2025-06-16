package ninegle.Readio.user.service

import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import ninegle.Readio.book.repository.PreferencesRepository
import ninegle.Readio.book.repository.ReviewRepository
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.library.repository.LibraryBookRepository
import ninegle.Readio.library.repository.LibraryRepository
import ninegle.Readio.mail.user.service.UserMailSender
import ninegle.Readio.subscription.repository.SubscriptionRepository
import ninegle.Readio.user.adapter.UserDetail
import ninegle.Readio.user.domain.BlackList
import ninegle.Readio.user.domain.RefreshToken
import ninegle.Readio.user.domain.User
import ninegle.Readio.user.dto.*
import ninegle.Readio.user.mapper.UserMapper.toDelete
import ninegle.Readio.user.mapper.UserMapper.toUser
import ninegle.Readio.user.repository.BlackListRepository
import ninegle.Readio.user.repository.TokenRepository
import ninegle.Readio.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
open class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val tokenRepository: TokenRepository,
    private val blackListRepository: BlackListRepository,
    private val userMailSender: UserMailSender,
    private val reviewRepository: ReviewRepository,
    private val preferencesRepository: PreferencesRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val libraryRepository: LibraryRepository,
    private val libraryBookRepository: LibraryBookRepository
) {



    //암호화 후 db에 회원가입 정보 저장
    //BaseResponse로 지정한 내용에 http 상태 코드를 수정 후 다시 ResponseEntity로 감싸서 보냄
    @Transactional
    open fun signup(dto: SignUpRequestDto) {
        // 이미 존재하는 이메일이면 가입을 막음
        if (userRepository.findByEmail(dto.email) != null) {
            throw BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS) // 409
        }
        val user = toUser(dto, passwordEncoder)
        userRepository.save(user)

        // 회원가입 환영 메일 전송
        userMailSender.sendSignupMail(user)
    }

    //객체 반환
    fun getUser(id: Long): User {
        return userRepository.findById(id).orElse(throw BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND))
    }

//    //가져온 객체가 없으면 에러, 있으면 user 반환
//    fun getById(id: Long): User {
//        return findById(id).orElseThrow<RuntimeException>(Supplier<RuntimeException> {
//            throw BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND)
//        })
//    }


    // user 객체를 UserDetail로 변환
    fun getDetails(id: Long): UserDetail {
        val findUser =  getUser(id)
        return UserDetail.UserDetailsMake(findUser)
    }


    //로그인
    @Transactional
    open fun login(dto: LoginRequestDto, response: HttpServletResponse) {
        //가입된 email과 password가 같은지 확인

        val user= userRepository.findByEmail(dto.email) ?: throw BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND)

        if (!passwordEncoder.matches(dto.password, user.password)) {
            throw BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND) //404
        }

        //가입된 정보가 일치하고 db에 refresh token이 존재하고 있으면 기간이 만료된게 확인되면 다시 재발급
        val refreshToken: String
        // 사용자 정보로 db에서 refresh token이 존재하는지 검색
        val findSavedToken = tokenRepository.findTop1ByUserIdOrderByIdDesc(user.id)


        if (findSavedToken != null) {
            if (!jwtTokenProvider.validate(findSavedToken.refreshToken )) {
                //refresh token이 존재하지만 시간이 만료된 경우 발급 및 갱신
                refreshToken = jwtTokenProvider.issueRefreshToken(user.id, user.role, user.email)
                findSavedToken.newSetRefreshToken(refreshToken)
            } else {
                //refresh token이 존재하며 유효기간도 아직 유효한 경우 기존 토큰 재사용
                refreshToken = findSavedToken.refreshToken
            }
        } else {
            //아예 토큰이 존재하지 않았던 경우로 새로 발급 및 저장
            refreshToken = jwtTokenProvider.issueRefreshToken(user.id, user.role, user.email)
            tokenRepository.save(RefreshToken(refreshToken, user))
        }

        val accessToken = jwtTokenProvider.issueAccessToken(user.id, user.role, user.email)

        //재발급 후 다시헤더에 넣어서 반환
        val loginResponseDto = LoginResponseDto(accessToken, refreshToken)
        response.setHeader("Authorization", "Bearer " + loginResponseDto.accessToken)
        response.setHeader("Refresh", loginResponseDto.refreshToken)
    }

    // Access Token 만료 시 Refresh Token으로 재발급하는 코드
    @Transactional
    open fun reissue(refreshToken: String, response: HttpServletResponse) {
        var refreshToken = refreshToken
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7)
        }

        // 토큰 유효성 확인 및 정보 추출 (사용자에 대한 권한이 아닌 토큰에 대한 유효성만 검사를 하므로 밑 부분처럼 추가 검사들이 필요합니다.)
        if (!jwtTokenProvider.validate(refreshToken)) {
            throw BusinessException(ErrorCode.INVALID_REFRESH_TOKEN) //401
        }


        //요청을 한 사람이 기존에 회원가입이 되어 있는 사용자가 맞는지 검사
        val userId = jwtTokenProvider.parseJwt(refreshToken).userId
        val user : User
        if (userRepository.findById(userId).isPresent) {
            //관리자 정보가 있다면 user 객체로 가져옴
             user = userRepository.findById(userId).get()
        }else{
            throw BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND) //404 반환
        }



        // DB에 있는 RefreshToken과 일치 여부 확인
        //클라이언트가 서버로 refresh token을 보냈을 때, 이 토큰이 "서버에서 발급한 것이 맞는지" 검증
        if (tokenRepository.findTop1ByUserIdOrderByIdDesc(userId) == null ) {
            throw BusinessException(ErrorCode.INVALID_REFRESH_TOKEN) //401 반환
        }
        val serverFindRefreshToken = tokenRepository.findTop1ByUserIdOrderByIdDesc(userId)

        //위에서 가져온 admin에 맞는 토큰 정보와 클라이언트가 요청으로 가져온 refresh Token이 같은지 다른지 확인해 위조 가능성을 체크
        require(serverFindRefreshToken?.refreshToken.equals(refreshToken)) { "RefreshToken 불일치 (위조 가능성!!!)" }

        //Refresh token이 유효하지만 access token 재발급 용도로 사용 후
        //Refresh Token이 노출되었을 수 있기 때문에, 사용 후에는 새로운 것으로 갱신하는 것이 안전하다
        val newAccessToken = jwtTokenProvider.issueAccessToken(user.id, user.role, user.email)
        val newRefreshToken = jwtTokenProvider.issueRefreshToken(user.id, user.role, user.email )

        serverFindRefreshToken?.newSetRefreshToken(newRefreshToken) // 새로 토큰을 발급 받아 기존 refresh token을 갱신
        val loginResponseDto = LoginResponseDto(newAccessToken, newRefreshToken) // 클라이언트에게 보내줄 용도

        response.setHeader("Authorization", "Bearer " + loginResponseDto.accessToken)
        response.setHeader("Refresh", loginResponseDto.refreshToken)
    }

    @Transactional
    open fun logout(accessToken: String, requestrefreshToken: RefreshTokenRequestDto) {
        //토큰 구조 먼저 확인
        var accessToken = accessToken
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7)
        }

        if (!jwtTokenProvider.validate(accessToken)) {
            throw BusinessException(ErrorCode.INVALID_ACCESS_TOKEN) //401
        }

        val userId: Long = jwtTokenProvider.parseJwt(accessToken).userId

        val findrefreshToken = tokenRepository.findTop1ByUserIdOrderByIdDesc(userId) ?: throw BusinessException(ErrorCode.INVALID_ACCESS_TOKEN) // 401 반환


        if (!findrefreshToken.refreshToken.equals(requestrefreshToken.refreshToken)) {
            throw BusinessException(ErrorCode.INVALID_REFRESH_TOKEN) // 401 반환
        }

        //토큰이 유효하다면, 이 토큰의 만료 시각을 가져온다. 블랙리스트에도 해당 만료 시간을 똑같이 넣어서 15분이면 15분 동안은 이 토큰을 사용하기 위해
        val expiration = jwtTokenProvider.getExpiration(
            accessToken
        ) //만료 시간 추출해서 현재 시간이 만료가 예정된 시간보다 작으면 그 토큰을 사용하지 못하게
        blackListRepository.save(BlackList(accessToken, expiration))
        tokenRepository.delete(findrefreshToken)
    }

    @Transactional
    open fun deleteUser(accessToken: String, deleteUserRequestDto: DeleteUserRequestDto) {
        //토큰 구조 먼저 확인
        var accessToken = accessToken
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7)
        }

        val delete = toDelete(deleteUserRequestDto)

        // 1. 토큰에서 유저 정보 추출
        val userId: Long = jwtTokenProvider.parseJwt(accessToken).userId

        // 2. DB에서 유저 조회
        val tokenUser = userRepository.findById(userId)
            .orElseThrow { BusinessException(ErrorCode.USER_NOT_FOUND) }

        //이메일 비교
        if (delete.email != tokenUser.email) {
            throw BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND) // 404
        }

        //refresh token 비교
        val dbGetToken  = tokenRepository.findTop1ByUserIdOrderByIdDesc(userId)?: throw BusinessException(ErrorCode.INVALID_REFRESH_TOKEN) //401

        if (!dbGetToken.refreshToken.equals(delete.refreshToken)) {
            throw BusinessException(ErrorCode.INVALID_REFRESH_TOKEN) //401
        }

        //비밀번호 비교
        if (!passwordEncoder.matches(delete.password, tokenUser.password)) {
            throw BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND) //404
        }

        // 라이브러리에 책, 라이브러리들 삭제
        val libraries = libraryRepository.findAllByUserId(userId)
        for (library in libraries) {
            val libraryBooks = libraryBookRepository.findByLibraryId(library.id)
            libraryBookRepository.deleteAll(libraryBooks)
        }
        libraryRepository.deleteAll(libraries)

        //관심도서 삭제
        val preferences = preferencesRepository.findAllByUserId(userId)
        preferencesRepository.deleteAll(preferences)

        //리뷰 삭제
        val reviews = reviewRepository.findAllByUserId(userId)
        reviewRepository.deleteAll(reviews)

        //구독 삭제
        val subscriptions = subscriptionRepository.findAllByUserId(userId)
        subscriptionRepository.deleteAll(subscriptions)

        //refresh token 삭제
        val refreshTokens = tokenRepository.findAllByUserId(userId)
        tokenRepository.deleteAll(refreshTokens)

        //유저 삭제
        userRepository.findById(userId).ifPresent { entity: User ->
            userRepository.delete(entity)
        }
    }

    fun getById(userId: Long): User {
        var user = userRepository.findById(userId)
        return user.get()
    }


}