package ninegle.Readio.global.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.web.multipart.MultipartFile

class ImageFileValidator : ConstraintValidator<ImageFile, MultipartFile?> {
    private val imageMimeTypes = setOf("image/png", "image/jpeg", "image/gif", "image/webp")

    override fun isValid(file: MultipartFile?, context: ConstraintValidatorContext): Boolean {
        return file == null || imageMimeTypes.contains(file.contentType)
    }
}