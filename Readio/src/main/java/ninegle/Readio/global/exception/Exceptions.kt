package ninegle.Readio.global.exception

import ninegle.Readio.global.exception.domain.ErrorCode

class BusinessException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)
