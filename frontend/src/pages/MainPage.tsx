import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import BookCover from "@/components/book/BookCover";
import bookService from "@/utils/api/bookService";
import { Skeleton } from "@/components/ui/skeleton";

interface Book {
  id: number;
  name: string;
  image: string | null;
  categoryMajor: string;
  categorySub: string;
  authorName: string;
  rating?: number | null;
}

const MainPage = () => {
  const navigate = useNavigate();
  const [popularBooks, setPopularBooks] = useState<Book[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  // API에서 인기 도서 불러오기
  useEffect(() => {
    const fetchPopularBooks = async () => {
      setIsLoading(true);
      try {
        // 기본 페이지 요청으로 도서 가져오기
        const response = await bookService.getBooks(
          `/books?keyword=&page=1&size=20`
        );

        console.log("API 응답:", response);

        if (response && response.status === 200) {
          setPopularBooks(response.data.books);
        } else {
          console.error("인기 도서를 불러오는데 실패했습니다.");
        }
      } catch (error) {
        console.error("Error fetching popular books:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchPopularBooks();
  }, []);

  // 가로 스크롤 함수
  const scrollSection = (sectionId: string, direction: "left" | "right") => {
    const section = document.getElementById(sectionId);
    if (section) {
      const scrollAmount = direction === "left" ? -400 : 400;
      section.scrollBy({ left: scrollAmount, behavior: "smooth" });
    }
  };

  // 책 상세 페이지로 이동하는 함수
  const navigateToBookDetail = (bookId: number) => {
    navigate(`/book/${bookId}`);
  };

  // 로딩 중 스켈레톤 UI
  const renderSkeletons = () => {
    return Array(6)
      .fill(0)
      .map((_, index) => (
        <div key={`skeleton-${index}`} className="flex-shrink-0 w-40 md:w-64">
          <div className="aspect-[2/3] mb-2">
            <Skeleton className="w-full h-full rounded" />
          </div>
          <Skeleton className="h-4 w-full mb-1" />
          <Skeleton className="h-3 w-2/3" />
        </div>
      ));
  };

  // BookCover 컴포넌트에 맞게 책 데이터 형식 변환
  const formatBookForCover = (book: Book) => {
    return {
      id: book.id,
      title: book.name,
      author: book.authorName,
      // BookCover 컴포넌트는 자체적으로 플레이스홀더 이미지를 처리함
      cover: book.image || "",
      category: book.categoryMajor,
      rating: book.rating || 0,
      isBestseller: false,
    };
  };

  // 표시할 책 수 제한 (첫 6권만)
  const booksToDisplay = popularBooks.slice(0, 6);

  return (
    <div>
      {/* 히어로 섹션 */}
      <section className="bg-gradient-to-r from-blue-500 to-blue-700 text-white py-12 mb-8">
        <div className="container mx-auto px-10">
          <div className="max-w-2xl">
            <h1 className="text-3xl md:text-4xl font-bold mb-4">
              당신의 지식과 상상력을 넓히는 공간
            </h1>
            <p className="text-lg md:text-xl mb-6">
              리디오에서 다양한 도서를 언제 어디서나 만나보세요
            </p>
            <div className="flex gap-3">
              
            </div>
          </div>
        </div>
      </section>

      {/* 인기 도서 섹션 */}
      <section className="container mx-auto mb-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-bold">인기 도서</h2>
          <div className="flex gap-2">
            <Button
              variant="outline"
              size="icon"
              className="h-8 w-8"
              onClick={() => scrollSection("popular-books", "left")}
            >
              <ChevronLeft className="h-4 w-4" />
            </Button>
            <Button
              variant="outline"
              size="icon"
              className="h-8 w-8"
              onClick={() => scrollSection("popular-books", "right")}
            >
              <ChevronRight className="h-4 w-4" />
            </Button>
          </div>
        </div>
        <div
          id="popular-books"
          className="flex overflow-x-auto pb-4 scrollbar-hide gap-4"
        >
          {isLoading ? (
            renderSkeletons()
          ) : booksToDisplay.length > 0 ? (
            booksToDisplay.map((book) => (
              <BookCover
                key={book.id}
                book={formatBookForCover(book)}
                onClick={navigateToBookDetail}
              />
            ))
          ) : (
            <div className="text-center w-full py-8 text-gray-500">
              책을 불러오지 못했습니다.
            </div>
          )}
        </div>
      </section>

      {/* 이벤트 섹션 */}
      <section className="container mx-auto px-4 mb-12">
        <h2 className="text-xl font-bold mb-4">이벤트 및 프로모션</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="bg-gradient-to-r from-purple-600 to-purple-800 rounded-lg p-6 text-white">
            <h3 className="text-lg font-bold mb-2">첫 가입 30일 무료</h3>
            <p className="mb-4">
              지금 가입하시면 30일간 무료로 모든 도서를 이용할 수 있습니다.
            </p>
            <Button className="bg-white text-purple-700 hover:bg-gray-100">
              자세히 보기
            </Button>
          </div>
        </div>
      </section>
    </div>
  );
};

export default MainPage;
