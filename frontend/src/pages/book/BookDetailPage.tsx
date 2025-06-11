import { useState, useEffect } from "react"
import { useParams, useNavigate } from "react-router-dom"
import {
  ArrowLeft,
  Bookmark,
  Heart,
  Calendar,
  Hash,
  Star,
  User,
  Building,
  ChevronDown,
  ChevronUp,
  BookOpen,
  MessageSquare,
  Plus,
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogClose,
} from "@/components/ui/dialog"
import { toast } from "sonner"
import ReviewCard from "@/components/review/ReviewCard"
import bookService from "@/utils/api/bookService"
import reviewService from "@/utils/api/reviewService"
import { libraryService, type Library } from "@/utils/api/libraryService"
import type { BookDetail } from "@/types/book"
import type { Review } from "@/utils/api/reviewService" // 리뷰 타입 import
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { useAuth } from "@/contexts/AuthContext"

const BookDetailPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [isLibraryAdded, setIsLibraryAdded] = useState(false)
  const [isWishlisted, setIsWishlisted] = useState(false)
  const [showFullDescription, setShowFullDescription] = useState(false)
  const [scrollPosition, setScrollPosition] = useState(0)
  const [reviews, setReviews] = useState<Review[]>([])
  const [totalReviews, setTotalReviews] = useState(0)
  const [averageRating, setAverageRating] = useState(0)
  const [isLoadingReviews, setIsLoadingReviews] = useState(true)
  const [book, setBook] = useState<BookDetail | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  // 라이브러리 관련 상태 추가
  const [libraries, setLibraries] = useState<Library[]>([])
  const [isLoadingLibraries, setIsLoadingLibraries] = useState(false)
  const [selectedLibraryId, setSelectedLibraryId] = useState<number | null>(null)
  const [isLibrarySelectDialogOpen, setIsLibrarySelectDialogOpen] = useState(false)
  const [newLibraryName, setNewLibraryName] = useState<string>("내 라이브러리1")
  const [isNewLibraryDialogOpen, setIsNewLibraryDialogOpen] = useState(false)

  // 스크롤 이벤트 핸들러 추가
  useEffect(() => {
    const handleScroll = () => {
      setScrollPosition(window.scrollY)
    }

    window.addEventListener("scroll", handleScroll)
    return () => {
      window.removeEventListener("scroll", handleScroll)
    }
  }, [])

  // 책 정보 로드
  useEffect(() => {
    const loadBookDetail = async () => {
      if (!id) return

      setIsLoading(true)
      try {
        const response = await bookService.getBookDetail(id)
        setBook(response.data.data)
      } catch (error) {
        console.error("책 정보 로드 중 오류 발생:", error)
        setError("책 정보를 불러오는 데 실패했습니다. 잠시 후 다시 시도해주세요.")
        toast.error("책 정보를 불러오는 데 실패했습니다", {
          description: "잠시 후 다시 시도해주세요.",
        })
      } finally {
        setIsLoading(false)
      }
    }

    loadBookDetail()
  }, [id])

  // 책 리뷰 로드
  useEffect(() => {
    const loadReviews = async () => {
      if (!id) return

      setIsLoadingReviews(true)
      try {
        const response = await reviewService.getLatestReviews(id, 3)
        const { reviews, summary } = response.data

        setReviews(reviews)
        setTotalReviews(summary.totalReviews)
        setAverageRating(summary.averageRating)
      } catch (error) {
        console.error("리뷰 로드 중 오류 발생:", error)
        toast.error("리뷰를 불러오는 데 실패했습니다", {
          description: "잠시 후 다시 시도해주세요.",
        })
      } finally {
        setIsLoadingReviews(false)
      }
    }

    if (id) {
      loadReviews()
    }
  }, [id])

  // 내 서재에 추가 버튼 클릭 시
  const handleAddToLibrary = async () => {
    if (!id) return

    // Check if user is authenticated
    if (!isAuthenticated) {
      navigate("/login")
      return
    }

    // 라이브러리 로딩 시작
    setIsLoadingLibraries(true)

    try {
      // 라이브러리 목록 로드
      const response = await libraryService.getLibraries()
      const libraryList = response.data.libraries || []
      setLibraries(libraryList)

      // 라이브러리가 없으면 라이브러리 생성 다이얼로그 표시
      if (libraryList.length === 0) {
        setIsNewLibraryDialogOpen(true)
      } else {
        // 라이브러리가 있으면 첫 번째 라이브러리를 기본 선택으로 설정
        setSelectedLibraryId(libraryList[0].library_id)
        // 라이브러리 선택 다이얼로그 표시
        setIsLibrarySelectDialogOpen(true)
      }
    } catch (error) {
      console.error("라이브러리 목록 로드 중 오류 발생:", error)
      toast.error("라이브러리 목록을 불러오는 데 실패했습니다", {
        description: "잠시 후 다시 시도해주세요.",
      })
    } finally {
      setIsLoadingLibraries(false)
    }
  }

  // 새 라이브러리 생성 및 책 추가
  const handleCreateLibraryAndAddBook = async () => {
    if (!id || !newLibraryName.trim()) return

    try {
      // 1. 새 라이브러리 생성
      const createResponse = await libraryService.createLibrary(newLibraryName)

      // 전체 응답 구조 상세 로깅
      console.log("라이브러리 생성 응답(전체):", createResponse)
      console.log("라이브러리 생성 응답(data):", createResponse.data)

      if (createResponse.data) {
        console.log("응답 데이터 키:", Object.keys(createResponse.data))
        if (createResponse.data.data) {
          console.log("응답 데이터 내부 데이터 키:", Object.keys(createResponse.data.data))
        }
      }

      // API 응답 구조에 따라 라이브러리 ID 추출 시도
      let newLibraryId

      // 여러 가능한 응답 구조 패턴 확인
      if (createResponse.data && createResponse.data.data && createResponse.data.data.id) {
        newLibraryId = createResponse.data.data.id
        console.log("패턴 1에서 ID 추출:", newLibraryId)
      } else if (createResponse.data && createResponse.data.id) {
        newLibraryId = createResponse.data.id
        console.log("패턴 2에서 ID 추출:", newLibraryId)
      } else if (createResponse.data && createResponse.data.data && createResponse.data.data.library_id) {
        newLibraryId = createResponse.data.data.library_id
        console.log("패턴 3에서 ID 추출:", newLibraryId)
      } else if (createResponse.data && createResponse.data.library_id) {
        newLibraryId = createResponse.data.library_id
        console.log("패턴 4에서 ID 추출:", newLibraryId)
      } else if (createResponse.data && createResponse.data.status === 201 && createResponse.data.data) {
        // API가 201 Created를 반환하는 경우, 생성된 라이브러리를 다시 조회
        console.log("라이브러리 생성 성공, ID를 찾기 위해 라이브러리 목록 조회 중...")
        const librariesResponse = await libraryService.getLibraries(0, 1)
        if (librariesResponse.data.libraries && librariesResponse.data.libraries.length > 0) {
          newLibraryId = librariesResponse.data.libraries[0].library_id
          console.log("최신 라이브러리에서 ID 추출:", newLibraryId)
        }
      }

      // 라이브러리 ID를 찾지 못한 경우, 임시 조치로 라이브러리 목록을 다시 조회
      if (!newLibraryId) {
        console.log("응답에서 ID를 찾지 못함, 라이브러리 목록 조회 중...")
        const librariesResponse = await libraryService.getLibraries(0, 10)
        console.log("최신 라이브러리 목록:", librariesResponse.data.libraries)

        // 방금 생성한 라이브러리를 찾기 (이름으로 필터링)
        const createdLibrary = librariesResponse.data.libraries?.find((lib) => lib.library_name === newLibraryName)

        if (createdLibrary) {
          newLibraryId = createdLibrary.library_id
          console.log("생성된 라이브러리 찾음, ID:", newLibraryId)
        } else if (librariesResponse.data.libraries && librariesResponse.data.libraries.length > 0) {
          // 못 찾았으면 첫 번째 라이브러리 사용
          newLibraryId = librariesResponse.data.libraries[0].library_id
          console.log("첫 번째 라이브러리 ID로 대체:", newLibraryId)
        }
      }

      if (!newLibraryId) {
        throw new Error("라이브러리 ID를 찾을 수 없습니다. 서버 응답 형식이 예상과 다릅니다.")
      }

      console.log("최종 사용할 라이브러리 ID:", newLibraryId)

      // 2. 찾은 라이브러리 ID로 책 추가
      await libraryService.addBookToLibrary(newLibraryId, id)

      // 성공 시 UI 업데이트
      setIsNewLibraryDialogOpen(false)
      setIsLibraryAdded(true)
      setIsDialogOpen(true)
      toast.success("내 서재에 추가되었습니다")

      // 라이브러리 목록 갱신 (필요시)
      const updatedLibraries = await libraryService.getLibraries()
      setLibraries(updatedLibraries.data.libraries || [])
    } catch (error) {
      console.error("라이브러리 생성 및 책 추가 중 오류 발생:", error)
      toast.error("라이브러리 생성 중 오류가 발생했습니다", {
        description: "잠시 후 다시 시도해주세요.",
      })
    }
  }

  // 선택한 라이브러리에 책 추가
  const handleAddBookToSelectedLibrary = async () => {
    if (!id || !selectedLibraryId) return

    try {
      await libraryService.addBookToLibrary(selectedLibraryId, id)
      setIsLibrarySelectDialogOpen(false)
      setIsLibraryAdded(true)
      setIsDialogOpen(true)
      toast.success("내 서재에 추가되었습니다")
    } catch (error) {
      console.error("책 추가 중 오류 발생:", error)
      toast.error("책 추가 중 오류가 발생했습니다", {
        description: "잠시 후 다시 시도해주세요.",
      })
    }
  }

  // 관심 도서에 추가 버튼 클릭
  const handleAddToWishlist = async () => {
    if (!id) return

    // Check if user is authenticated
    if (!isAuthenticated) {
      navigate("/login")
      return
    }

    try {
      await bookService.toggleWishlist(id)
      setIsWishlisted(!isWishlisted)
      toast.success(isWishlisted ? "관심 도서에서 제거되었습니다" : "관심 도서에 추가되었습니다")
    } catch (error) {
      console.error("관심 도서 토글 중 오류 발생:", error)
      toast.error("요청 처리 중 오류가 발생했습니다", {
        description: "잠시 후 다시 시도해주세요.",
      })
    }
  }

  const handleReadBook = () => {
    setIsDialogOpen(false)

    // 새 탭에서 열기
    window.open(`/read/${id}`, "_blank")
  }

  // 뒤로 가기
  const handleGoBack = () => {
    navigate(-1)
  }

  // 설명 더보기/접기 토글
  const toggleDescription = () => {
    setShowFullDescription(!showFullDescription)
  }

  // 리뷰 페이지로 이동
  const navigateToReviews = () => {
    navigate(`/book/${id}/reviews`)
  }

  // 리뷰 렌더링
  const renderReviews = () => {
    if (isLoadingReviews) {
      return (
        <div className="text-center py-4">
          <div className="inline-block animate-spin rounded-full h-6 w-6 border-b-2 border-blue-500"></div>
          <p className="mt-2 text-gray-600">리뷰를 불러오는 중...</p>
        </div>
      )
    }

    if (reviews.length === 0) {
      return (
        <div className="text-center py-6 bg-gray-50 rounded-lg">
          <MessageSquare className="h-10 w-10 text-gray-400 mx-auto mb-2" />
          <p className="text-gray-600">아직 등록된 리뷰가 없습니다.</p>
          <Button variant="outline" size="sm" className="mt-3" onClick={navigateToReviews}>
            첫 리뷰를 작성해보세요!
          </Button>
        </div>
      )
    }

    return (
      <div className="space-y-4">
        {reviews.map((review) => (
          <ReviewCard
            key={review.id}
            id={review.id}
            username={review.email.split("@")[0]} // 이메일에서 아이디 부분만 표시
            user_id={review.email}
            rating={review.rating}
            date={new Date(review.createdAt).toISOString().split("T")[0]} // 날짜 형식 변환
            content={review.text}
          />
        ))}
      </div>
    )
  }

  // 로딩 중 표시
  if (isLoading) {
    return (
      <div className="container mx-auto py-12 flex flex-col items-center justify-center">
        <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
        <p className="mt-4 text-gray-600">책 정보를 불러오는 중...</p>
      </div>
    )
  }

  // 에러 표시
  if (error || !book) {
    return (
      <div className="container mx-auto py-12 flex flex-col items-center justify-center">
        <div className="bg-red-100 p-6 rounded-lg text-center max-w-md">
          <h2 className="text-xl font-semibold text-red-700 mb-2">오류 발생</h2>
          <p className="text-gray-700">{error || "책 정보를 불러올 수 없습니다."}</p>
          <Button className="mt-4" onClick={() => navigate(-1)}>
            뒤로 가기
          </Button>
        </div>
      </div>
    )
  }

  // 기본 이미지 URL 옵션들 (book.image가 null인 경우)
  const placeholderOptions = [
    // 책 스타일 플레이스홀더 (푸른색)
    "https://placehold.co/400x600/e8eaf2/4a6fa5?text=No+Cover+Available&font=montserrat",
    // 책 스타일 플레이스홀더 (빨간색)
    "https://placehold.co/400x600/f5e6e8/9e3f3f?text=Cover+Coming+Soon&font=montserrat",
    // 책 스타일 플레이스홀더 (녹색)
    "https://placehold.co/400x600/e8f0e6/3e733f?text=No+Image+Available&font=montserrat",
  ]

  // 책 ID를 기반으로 일관된 플레이스홀더 이미지 선택 (책마다 다른 이미지가 표시되지 않도록)
  const placeholderIndex = 0 // 첫 번째 옵션 항상 사용 (원하는 경우 id를 기반으로 계산 가능)
  const bookImage = book.image || placeholderOptions[placeholderIndex]

  return (
    <div className="pb-12">
      <div className="container mx-auto">
        {/* 상단 네비게이션 */}
        <div className="mb-6">
          <Button variant="ghost" onClick={handleGoBack} className="pl-0">
            <ArrowLeft className="h-5 w-5 mr-2" />
            돌아가기
          </Button>
        </div>

        {/* 고정 헤더 및 콘텐츠 레이아웃 */}
        <div className="grid grid-cols-1 md:grid-cols-12 gap-6 md:gap-14">
          {/* 왼쪽: 스크롤해도 고정되는 책 커버 및 버튼 영역 */}
          <div className="md:col-span-3">
            <div className={`md:sticky md:top-24`}>
              <div className="flex flex-col items-center">
                <div className="w-48 md:w-full shadow-lg rounded-lg overflow-hidden bg-white">
                  <img src={bookImage || "/placeholder.svg"} alt={book.name} className="w-full object-cover" />
                </div>

                {/* 별점 및 리뷰 수 - 모바일에서만 표시 */}
                <div className="md:hidden flex items-center mt-4 mb-4">
                  <div className="flex items-center bg-yellow-50 px-3 py-1.5 rounded-full">
                    <Star className="h-5 w-5 fill-yellow-500 text-yellow-500" />
                    <span className="ml-1 font-medium text-yellow-700">{averageRating}</span>
                    <span className="text-gray-500 text-sm ml-1">({totalReviews})</span>
                  </div>
                </div>

                {/* 버튼 영역 */}
                <div className="w-full mt-6 flex flex-col gap-3">
                  <Button
                    className={`w-full ${
                      isLibraryAdded ? "bg-green-600 hover:bg-green-700" : "bg-blue-500 hover:bg-blue-600"
                    }`}
                    onClick={handleAddToLibrary}
                  >
                    {isLibraryAdded ? (
                      <>
                        <BookOpen className="h-5 w-5 mr-2" />
                        서재에 추가됨
                      </>
                    ) : (
                      <>
                        <Bookmark className="h-5 w-5 mr-2" />내 서재에 추가
                      </>
                    )}
                  </Button>

                  <Button
                    variant={isWishlisted ? "secondary" : "outline"}
                    className="w-full"
                    onClick={handleAddToWishlist}
                  >
                    <Heart className={`h-5 w-5 mr-2 ${isWishlisted ? "fill-red-500 text-red-500" : ""}`} />
                    {isWishlisted ? "관심 도서 추가됨" : "관심 도서에 추가"}
                  </Button>
                </div>
              </div>
            </div>
          </div>

          {/* 오른쪽: 책 상세 정보 */}
          <div className="md:col-span-9">
            {/* 제목 및 기본 정보 */}
            <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-4">
              <div>
                <div className="flex flex-wrap gap-2 mb-2">
                  <Badge className="bg-blue-500">{book.category.major}</Badge>
                  {book.category.sub && <Badge variant="outline">{book.category.sub}</Badge>}
                </div>
                <h1 className="text-2xl md:text-3xl font-bold">{book.name}</h1>
              </div>

              {/* 별점 및 리뷰 수 - 데스크탑에서만 */}
              <div className="hidden md:flex items-center">
                <div className="flex items-center bg-yellow-50 px-3 py-1.5 rounded-full">
                  <Star className="h-5 w-5 fill-yellow-500 text-yellow-500" />
                  <span className="ml-1 font-medium text-yellow-700">{averageRating}</span>
                  <span className="text-gray-500 text-sm ml-1">({totalReviews})</span>
                </div>
              </div>
            </div>

            {/* 저자, 출판사 정보 */}
            <div className="flex flex-wrap gap-y-1 gap-x-6 mt-4 text-sm">
              <div className="flex items-center">
                <User className="h-4 w-4 text-gray-500 mr-2" />
                <span className="text-gray-700">저자: </span>
                <span className="ml-1 font-medium">{book.author.name}</span>
              </div>
              <div className="flex items-center">
                <Building className="h-4 w-4 text-gray-500 mr-2" />
                <span className="text-gray-700">출판사: </span>
                <span className="ml-1 font-medium">{book.publisher.name}</span>
              </div>
              <div className="flex items-center">
                <Calendar className="h-4 w-4 text-gray-500 mr-2" />
                <span className="text-gray-700">출간일: </span>
                <span className="ml-1 font-medium">{book.pubDate}</span>
              </div>
              {/* 페이지 정보는 API 응답에 없어서 생략 */}
              <div className="flex items-center">
                <Hash className="h-4 w-4 text-gray-600 mr-2" />
                <span className="text-sm">ISBN: {book.isbn}</span>
              </div>
              {book.ecn && (
                <div className="flex items-center">
                  <Hash className="h-4 w-4 text-gray-600 mr-2" />
                  <span className="text-sm">e-ISBN: {book.ecn}</span>
                </div>
              )}
            </div>
            <hr className="mt-6" />
            <div className="mt-6 md:mt-6 ">
              {/* 책 설명 */}
              <div>
                <h3 className="font-medium text-lg mb-4">도서 소개</h3>
                <div className={`text-gray-700 leading-relaxed ${!showFullDescription && "line-clamp-4"}`}>
                  {book.description}
                </div>
                {book.description && book.description.length > 200 && (
                  <Button variant="ghost" className="mt-2 text-blue-500 flex items-center" onClick={toggleDescription}>
                    {showFullDescription ? (
                      <>
                        접기 <ChevronUp className="ml-1 h-4 w-4" />
                      </>
                    ) : (
                      <>
                        더 보기 <ChevronDown className="ml-1 h-4 w-4" />
                      </>
                    )}
                  </Button>
                )}
              </div>
              <hr className="mt-6" />
              <div className="flex items-center justify-between mb-4 mt-6">
                <h3 className="font-medium text-lg">독자 리뷰</h3>
                <Button variant="outline" size="sm" onClick={navigateToReviews}>
                  리뷰 작성하기
                </Button>
              </div>

              {/* API에서 가져온 리뷰 렌더링 */}
              {renderReviews()}

              {/* 리뷰 더 보기 버튼 추가 */}
              {!isLoadingReviews && reviews.length > 0 && (
                <div className="flex justify-center mt-4">
                  <Button variant="outline" className="flex items-center" onClick={navigateToReviews}>
                    <MessageSquare className="mr-2 h-4 w-4" />
                    리뷰 {totalReviews}개 모두 보기
                  </Button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* 내 서재에 추가 확인 다이얼로그 */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>서재에 추가되었습니다</DialogTitle>
            <DialogDescription>{book.name}이(가) 내 서재에 추가되었습니다.</DialogDescription>
          </DialogHeader>
          <div className="flex justify-center my-4">
            <img src={bookImage || "/placeholder.svg"} alt={book.name} className="h-60 w-40 rounded-md shadow-md" />
          </div>
          <div className="flex flex-col gap-2 sm:flex-row">
            <Button variant="outline" onClick={() => setIsDialogOpen(false)} className="sm:flex-1">
              책 더 구경하기
            </Button>
            <Button onClick={handleReadBook} className="bg-blue-500 hover:bg-blue-600 sm:flex-1">
              지금 읽기
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      {/* 라이브러리 선택 다이얼로그 */}
      <Dialog open={isLibrarySelectDialogOpen} onOpenChange={setIsLibrarySelectDialogOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>책을 추가할 라이브러리 선택</DialogTitle>
            <DialogDescription>{book.name}을(를) 추가할 라이브러리를 선택해주세요.</DialogDescription>
          </DialogHeader>

          {isLoadingLibraries ? (
            <div className="py-4 text-center">
              <div className="inline-block animate-spin rounded-full h-6 w-6 border-b-2 border-blue-500"></div>
              <p className="mt-2 text-gray-600">라이브러리를 불러오는 중...</p>
            </div>
          ) : (
            <div className="py-4">
              <RadioGroup
                value={selectedLibraryId?.toString()}
                onValueChange={(value) => setSelectedLibraryId(Number.parseInt(value))}
                className="space-y-2"
              >
                {libraries.map((library) => (
                  <div key={library.library_id} className="flex items-center space-x-2 border p-3 rounded-md">
                    <RadioGroupItem value={library.library_id.toString()} id={`library-${library.library_id}`} />
                    <Label htmlFor={`library-${library.library_id}`} className="flex-1">
                      {library.library_name}
                    </Label>
                  </div>
                ))}
              </RadioGroup>

              <div className="mt-4">
                <Button
                  variant="outline"
                  onClick={() => {
                    setIsLibrarySelectDialogOpen(false)
                    setIsNewLibraryDialogOpen(true)
                  }}
                  className="w-full"
                >
                  <Plus className="h-4 w-4 mr-2" />새 라이브러리 생성
                </Button>
              </div>
            </div>
          )}

          <DialogFooter className="gap-2 sm:gap-0 space-x-2">
            <DialogClose asChild>
              <Button variant="outline">취소</Button>
            </DialogClose>
            <Button
              className="bg-blue-500 hover:bg-blue-600"
              onClick={handleAddBookToSelectedLibrary}
              disabled={!selectedLibraryId}
            >
              추가하기
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 새 라이브러리 생성 다이얼로그 */}
      <Dialog
        open={isNewLibraryDialogOpen}
        onOpenChange={(open) => {
          // 닫을 때 라이브러리 선택 다이얼로그가 열려있었다면 다시 열기
          if (!open && isLibrarySelectDialogOpen && libraries.length > 0) {
            setIsLibrarySelectDialogOpen(true)
          }
          setIsNewLibraryDialogOpen(open)
        }}
      >
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>새 라이브러리 생성</DialogTitle>
            <DialogDescription>새 라이브러리를 생성하고 {book.name}을(를) 추가합니다.</DialogDescription>
          </DialogHeader>

          <div className="py-4">
            <Label htmlFor="library-name" className="mb-2 block">
              라이브러리 이름
            </Label>
            <Input
              id="library-name"
              value={newLibraryName}
              onChange={(e) => setNewLibraryName(e.target.value)}
              placeholder="라이브러리 이름을 입력하세요"
            />
          </div>

          <DialogFooter className="gap-2 sm:gap-0">
            <Button
              variant="outline"
              onClick={() => {
                setIsNewLibraryDialogOpen(false)
                // 라이브러리가 있으면 선택 다이얼로그로 돌아가기
                if (libraries.length > 0) {
                  setIsLibrarySelectDialogOpen(true)
                }
              }}
            >
              취소
            </Button>
            <Button
              className="bg-blue-500 hover:bg-blue-600"
              onClick={handleCreateLibraryAndAddBook}
              disabled={!newLibraryName.trim()}
            >
              생성하고 추가하기
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

export default BookDetailPage
