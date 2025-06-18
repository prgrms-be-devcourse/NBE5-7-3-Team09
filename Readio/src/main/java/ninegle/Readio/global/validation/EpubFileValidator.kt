package ninegle.Readio.global.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.web.multipart.MultipartFile

class EpubFileValidator : ConstraintValidator<EpubFile, MultipartFile?> {
    override fun isValid(file: MultipartFile?, context: ConstraintValidatorContext): Boolean {
        return file == null || file.originalFilename?.lowercase()?.endsWith(".epub") == true
    }
}