package ninegle.Readio.adapter.util

object NCloudStorageUtils {

    private lateinit var endPoint: String
    private lateinit var bucketName: String

    fun init(endPoint: String, bucketName: String) {
        this.endPoint = endPoint
        this.bucketName = bucketName
    }

    fun toImageUrl(image: String): String {
        return "${endPoint}/${bucketName}/$image"
    }

}