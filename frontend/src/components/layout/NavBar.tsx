// NavBar.tsx - 드롭다운 위치 수정
import { useState, useEffect } from "react";
import {
  Search,
  BookOpen,
  User,
  LogIn,
  UserPlus,
  Heart,
  LogOut,
  ChevronDown,
  ChevronUp,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import logo from "../../assets/ReadioLogo.png";
import { categoryService, Category } from "@/utils/api/categoryService";
import { toast } from "sonner";

const Navbar = () => {
  const { isAuthenticated, logout } = useAuth();
  const [showCategories, setShowCategories] = useState(true);
  const [lastScrollY, setLastScrollY] = useState(0);
  const [searchQuery, setSearchQuery] = useState("");
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoadingCategories, setIsLoadingCategories] = useState(true);
  const [expandedCategory, setExpandedCategory] = useState<number | null>(null);
  const navigate = useNavigate();

  // 카테고리 목록 불러오기
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        setIsLoadingCategories(true);
        const response = await categoryService.getCategories();
        if (response.status === 200) {
          setCategories(response.data.categories);
        } else {
          throw new Error("카테고리 목록을 불러오는데 실패했습니다.");
        }
      } catch (error) {
        console.error("Error fetching categories:", error);
        toast.error("카테고리 불러오기 실패", {
          description: "카테고리 목록을 불러오는 중 오류가 발생했습니다.",
        });
      } finally {
        setIsLoadingCategories(false);
      }
    };

    fetchCategories();
  }, []);

  // 스크롤 방향에 따라 카테고리 표시/숨김 처리
  useEffect(() => {
    const handleScroll = () => {
      const currentScrollY = window.scrollY;
      if (currentScrollY > lastScrollY) {
        setShowCategories(false);
        // 스크롤 다운 시 열린 서브카테고리 닫기
        setExpandedCategory(null);
      } else {
        setShowCategories(true);
      }
      setLastScrollY(currentScrollY);
    };

    window.addEventListener("scroll", handleScroll);
    return () => {
      window.removeEventListener("scroll", handleScroll);
    };
  }, [lastScrollY]);

  // 외부 클릭 감지하여 드롭다운 닫기
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (expandedCategory !== null) {
        const target = e.target as HTMLElement;
        // 드롭다운 또는 버튼 외부 클릭 시 닫기
        if (!target.closest(".category-container")) {
          setExpandedCategory(null);
        }
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [expandedCategory]);

  // 메인 카테고리 클릭 처리 - 서브 카테고리 토글
  const handleCategoryClick = (categoryId: number, e: React.MouseEvent) => {
    e.stopPropagation(); // 이벤트 버블링 방지

    if (expandedCategory === categoryId) {
      // 이미 열려있는 카테고리를 다시 클릭하면 닫기
      setExpandedCategory(null);
    } else {
      // 다른 카테고리를 클릭하면 해당 카테고리 열기
      setExpandedCategory(categoryId);
    }
  };

  // 서브 카테고리 클릭 처리
  const handleSubCategoryClick = (
    category: string,
    subIndex: number,
    e: React.MouseEvent
  ) => {
    e.stopPropagation(); // 이벤트 버블링 방지

    // category에서 " 일반" 문자열 제거
    const cleanCategory = category.replace(" 일반", "");

    navigate(`/books?category_major=${cleanCategory}&category_sub=${subIndex}`);
    // 서브 카테고리 클릭 후 드롭다운 닫기
    setExpandedCategory(null);
  };

  // 전체 카테고리 보기 처리
  const handleAllCategoriesClick = () => {
    navigate("/books");
    setExpandedCategory(null);
  };

  // 내 라이브러리로 이동
  const navigateToLibrary = () => {
    navigate("/library");
  };

  // 관심 도서로 이동
  const navigateToPreference = () => {
    navigate("/preference");
  };

  // 마이페이지로 이동
  const navigateToMyPage = () => {
    navigate("/my-page");
  };

  // 로그아웃 함수
  const handleLogout = () => {
    logout();
    navigate("/");
  };

  // 검색 처리 함수 - navigate를 사용하여 SPA 방식으로 이동
  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/books?keyword=${searchQuery}`);
      setSearchQuery("");
    }
  };

  return (
    <header className="w-full border-b border-gray-200 bg-white fixed top-0 left-0 right-0 z-10 pt-1">
      {/* 헤더 내용 */}
      <div className="container mx-auto px-4 h-16 flex items-center justify-between">
        {/* 좌측: 로고 및 검색바 */}
        <div className="flex items-center gap-4 flex-1 max-w-xl">
          {/* 로고 */}
          <Link to="/" className="flex items-center gap-2">
            <img src={logo} alt="Readio logo" className="w-36 h-w-36" />
          </Link>

          {/* 검색바 */}
          <div className="relative flex-1 hidden sm:flex items-center">
            <form onSubmit={handleSearch} className="w-full">
              <Search className="absolute left-2 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
              <Input
                type="text"
                placeholder="책 검색..."
                className="pl-8 pr-20 w-full h-9 rounded-lg focus:ring-2 focus:ring-blue-600"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
              <Button
                type="submit"
                variant="default"
                size="sm"
                className="absolute right-1 top-1/2 -translate-y-1/2 h-7 bg-blue-500 hover:bg-blue-600"
              >
                검색하기
              </Button>
            </form>
          </div>
        </div>

        {/* 우측 버튼들은 동일하게 유지 */}
        <div className="flex items-center gap-1 sm:gap-2">
          {isAuthenticated ? (
            <>
              <Button
                variant="ghost"
                className="hidden md:flex gap-1 items-center"
                onClick={navigateToLibrary}
              >
                <BookOpen className="h-5 w-5" />
                <span>내 라이브러리</span>
              </Button>

              <Button
                variant="ghost"
                className="hidden md:flex gap-1 items-center"
                onClick={navigateToPreference}
              >
                <Heart className="h-5 w-5" />
                <span>관심 도서</span>
              </Button>

              <Button
                variant="ghost"
                className="hidden md:flex gap-1 items-center"
                onClick={navigateToMyPage}
              >
                <User className="h-5 w-5" />
                <span>마이페이지</span>
              </Button>

              <Button
                variant="ghost"
                className="hidden md:flex gap-1 items-center text-red-600"
                onClick={handleLogout}
              >
                <LogOut className="h-5 w-5" />
                <span>로그아웃</span>
              </Button>

              <div className="flex md:hidden">
                <Button variant="ghost" size="icon" onClick={navigateToLibrary}>
                  <BookOpen className="h-5 w-5" />
                </Button>
                <Button
                  variant="ghost"
                  size="icon"
                  onClick={navigateToPreference}
                >
                  <Heart className="h-5 w-5" />
                </Button>
                <Button variant="ghost" size="icon" onClick={navigateToMyPage}>
                  <User className="h-5 w-5" />
                </Button>
                <Button
                  variant="ghost"
                  size="icon"
                  onClick={handleLogout}
                  className="text-red-600"
                >
                  <LogOut className="h-5 w-5" />
                </Button>
              </div>
            </>
          ) : (
            <>
              <Link to="/login">
                <Button variant="ghost" className="">
                  <LogIn className="h-5 w-5" />
                  로그인
                </Button>
              </Link>
              <Link to="/signup">
                <Button
                  variant="default"
                  className=" bg-blue-500 hover:bg-blue-600"
                >
                  <UserPlus className="h-5 w-5" />
                  회원가입
                </Button>
              </Link>
            </>
          )}
        </div>
      </div>

      {/* 카테고리 메뉴 - 수정된 부분 */}
      <div
        className={`container mx-auto px-4 transition-all duration-300 ${
          showCategories
            ? "opacity-100 py-2"
            : "opacity-0 max-h-0 overflow-hidden py-0"
        }`}
      >
        <div className="flex gap-2 overflow-x-auto scrollbar-hide">
          {/* 전체 카테고리 버튼 */}
          <Button
            variant="ghost"
            className="text-sm h-8 px-3 flex items-center whitespace-nowrap"
            onClick={handleAllCategoriesClick}
          >
            전체
          </Button>

          {/* 카테고리 목록 */}
          {isLoadingCategories
            ? Array(10)
                .fill(0)
                .map((_, index) => (
                  <Button
                    key={`skeleton-${index}`}
                    variant="ghost"
                    className="text-sm h-8 px-3 flex items-center whitespace-nowrap opacity-50"
                    disabled
                  >
                    <div className="w-16 h-4 animate-pulse bg-gray-200 rounded"></div>
                  </Button>
                ))
            : categories.map((category) => (
                <div
                  key={category.id}
                  className="category-container"
                  style={{ position: "relative", display: "inline-block" }}
                >
                  <Button
                    variant={
                      expandedCategory === category.id ? "default" : "ghost"
                    }
                    className={`text-sm h-8 px-3 flex items-center whitespace-nowrap gap-1 ${
                      expandedCategory === category.id
                        ? "bg-blue-500 text-white"
                        : ""
                    }`}
                    onClick={(e) => handleCategoryClick(category.id, e)}
                  >
                    {category.major}
                    {expandedCategory === category.id ? (
                      <ChevronUp className="h-4 w-4" />
                    ) : (
                      <ChevronDown className="h-4 w-4" />
                    )}
                  </Button>

                  {/* 서브 카테고리 드롭다운 - z-index와 위치 수정 */}
                  {expandedCategory === category.id && (
                    <div
                      className="bg-white shadow-lg rounded-md border border-gray-200 py-2 overflow-y-auto mt-1"
                      style={{
                        position: "fixed",
                        left: `${
                          document
                            .querySelector(
                              `.category-container:nth-child(${
                                categories.findIndex(
                                  (c) => c.id === category.id
                                ) + 2
                              })`
                            )
                            ?.getBoundingClientRect().left
                        }px`,
                        zIndex: 9999,
                        width: "200px",
                        maxHeight: "300px",
                      }}
                    >
                      {category.subs.map((sub, index) => (
                        <button
                          key={index}
                          className="w-full text-left px-4 py-2 hover:bg-gray-100 text-sm cursor-pointer"
                          onClick={(e) => handleSubCategoryClick(sub, index, e)}
                        >
                          {sub}
                        </button>
                      ))}
                    </div>
                  )}
                </div>
              ))}
        </div>
      </div>
    </header>
  );
};

export default Navbar;
