import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { authService } from "@/utils/api/authService";
import { useAdminAuth } from "@/contexts/AdminAuthContext"; // ✅ 반드시 필요
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { toast } from "sonner";
import { cn } from "@/lib/utils";
import {
  BookIcon,
  LogOutIcon,
  PencilIcon,
  TrashIcon,
  UploadIcon,
  SaveIcon,
  SearchIcon,
} from "lucide-react";
import publisherService from "@/utils/api/publisherService";
import { Publisher } from "@/types/publisher";
import { Book, BookDetail, Category as BookCategory } from "@/types/book";
import { categoryService, Category } from "@/utils/api/categoryService";
import bookService from "@/utils/api/bookService";
import axios, { AxiosError } from "axios";

function isValidDateFormat(dateString: string): boolean {
  // YYYY-MM-DD 형식 검증
  const regex = /^\d{4}-\d{2}-\d{2}$/;
  if (!regex.test(dateString)) return false;

  // 날짜 유효성 검증
  const date = new Date(dateString);
  return !isNaN(date.getTime());
}

const formatDate = (date: Date): string => {
  return date.toISOString().split("T")[0]; // YYYY-MM-DD 형식으로 반환
};

// 도서 폼 인터페이스
interface BookFormData {
  id?: number;
  name: string;
  authorName: string;
  publisherName: string;
  categorySub: string;
  categoryMajor: string;
  description: string;
  isbn: string;
  ecn: string;
  pubDate: Date | undefined;
  image: File | null;
  epubFile: File | null;
  imageUrl?: string;
  publishedDateString: string;
}

// 초기 빈 폼 데이터
const emptyBookForm: BookFormData = {
  name: "",
  authorName: "",
  publisherName: "",
  categorySub: "",
  categoryMajor: "",
  description: "",
  isbn: "",
  ecn: "",
  pubDate: undefined,
  image: null,
  epubFile: null,
  publishedDateString: "",
};

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

const AdminDashboard: React.FC = () => {
  const navigate = useNavigate();
  const { admin, logout } = useAdminAuth();
  const [activeTab, setActiveTab] = useState("booksList");
  const [books, setBooks] = useState<BookDetail[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("");
  const [bookForm, setBookForm] = useState<BookFormData>(emptyBookForm);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [coverPreview, setCoverPreview] = useState<string | null>(null);
  const [editingBookId, setEditingBookId] = useState<number | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [bookToDelete, setBookToDelete] = useState<number | null>(null);
  const [publishers, setPublishers] = useState<Publisher[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [subCategories, setSubCategories] = useState<string[]>([]);
  const [dateInputError, setDateInputError] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [totalBooks, setTotalBooks] = useState(0);
  const [publisherModalOpen, setPublisherModalOpen] = useState(false);
  const [newPublisherName, setNewPublisherName] = useState("");
  const [isAddingPublisher, setIsAddingPublisher] = useState(false);

  // API 데이터 로드
  useEffect(() => {
    const loadData = async () => {
      try {
        setIsLoading(true);

        // 출판사 데이터 로드
        const publishersData = await publisherService.getAllPublishers();
        setPublishers(publishersData);

        // 카테고리 데이터 로드
        const categoriesData = await categoryService.getCategories();
        setCategories(categoriesData.data.categories);

        // 책 데이터 로드
        await loadBooks();
      } catch (error) {
        console.error("데이터 로딩 중 오류 발생:", error);
        toast.error("데이터를 불러오는 중 오류가 발생했습니다.");
      } finally {
        setIsLoading(false);
      }
    };

    loadData();
  }, []);

  // 책 데이터 로드 함수
  const loadBooks = async (page = 1, category = "") => {
    try {
      setIsLoading(true);
      let url = `/books?page=${page}&size=${pageSize}`;

      if (category) {
        url += `&category_major=${encodeURIComponent(category)}`;
      }

      if (searchTerm) {
        url += `&name=${encodeURIComponent(searchTerm)}`;
      }

      const response = await bookService.getBooks(url);

      // books 데이터와 pagination 정보 설정
      if (response && response.data && response.data.books) {
        setBooks(response.data.books);

        if (response.data.pagination) {
          setTotalPages(response.data.pagination.totalPages);
          setCurrentPage(response.data.pagination.currentPage);
          setTotalBooks(response.data.pagination.totalElements);
        }
      }
    } catch (error) {
      console.error("책 목록 로딩 중 오류 발생:", error);
      toast.error("책 목록을 불러오는 중 오류가 발생했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleOpenPublisherModal = () => {
    setNewPublisherName("");
    setPublisherModalOpen(true);
  };

  // 3. 출판사 추가 함수
  const handleAddPublisher = async () => {
    if (!newPublisherName.trim()) {
      toast.error("출판사명을 입력해주세요.");
      return;
    }

    try {
      setIsAddingPublisher(true);

      // API 호출로 새 출판사 추가 - 경로와 요청 형식 수정
      const response = await axios.post(
        `${API_BASE_URL}/admin/publishers`,
        {
          name: newPublisherName,
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
            "Content-Type": "application/json",
          },
        }
      );

      // 성공 시 출판사 목록 갱신
      const newPublisher = response.data;
      // 새 출판사를 목록에 추가
      setPublishers([
        ...publishers,
        { id: newPublisher.id || Date.now(), name: newPublisherName },
      ]);

      // 폼에 새 출판사 선택
      setBookForm((prev) => ({
        ...prev,
        publisherName: newPublisherName,
      }));

      toast.success("출판사가 성공적으로 추가되었습니다.");
      setPublisherModalOpen(false);
    } catch (error: unknown) {
      const err = error as AxiosError;
      console.error("출판사 추가 중 오류 발생:", error);
      if (err.response && err.response.data && err.response.data.message) {
        toast.error(`출판사 추가 실패: ${err.response.data.message}`);
      } else {
        toast.error("출판사 추가 중 오류가 발생했습니다.");
      }
    } finally {
      setIsAddingPublisher(false);
    }
  };

  // 카테고리 메이저 변경 시 서브 카테고리 업데이트
  const handleCategoryMajorChange = (major: string) => {
    const selectedCategory = categories.find((cat) => cat.major === major);

    setBookForm((prev) => ({
      ...prev,
      categoryMajor: major,
      categorySub: "", // 메이저 카테고리 변경 시 서브 카테고리 초기화
    }));

    if (selectedCategory) {
      setSubCategories(selectedCategory.subs);
    } else {
      setSubCategories([]);
    }
  };

  // 페이지 변경
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    loadBooks(page, selectedCategory);
  };

  // 카테고리 변경
  const handleCategoryChange = (category: string) => {
    setSelectedCategory(category);
    setCurrentPage(1);
    loadBooks(1, category);
  };

  // 검색 실행
  const handleSearch = () => {
    setCurrentPage(1);
    loadBooks(1, selectedCategory);
  };

  // 검색어 엔터 처리
  const handleSearchKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  // 로그아웃 처리
  const handleLogout = async () => {
    await authService.logout(); // ✅ 공통 logout 사용
    navigate("/admin/login");
  };

  // 날짜 문자열 직접 입력 처리
  const handleDateStringChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const dateString = e.target.value;
    setBookForm((prev) => ({
      ...prev,
      publishedDateString: dateString,
    }));

    // 날짜 형식 검증
    if (dateString && !isValidDateFormat(dateString)) {
      setDateInputError("YYYY-MM-DD 형식으로 입력해주세요.");
    } else {
      setDateInputError("");
      // 유효한 날짜면 Date 객체도 업데이트
      if (dateString) {
        setBookForm((prev) => ({
          ...prev,
          pubDate: new Date(dateString),
        }));
      }
    }
  };

  const handlePublisherChange = (value: string) => {
    if (value === "직접입력") {
      handleOpenPublisherModal();
    } else {
      setBookForm((prev) => ({
        ...prev,
        publisherName: value,
      }));
    }
  };

  // 도서 편집 시작
  const handleEditBook = async (bookId: number) => {
    try {
      setIsLoading(true);

      // 책 상세 정보 가져오기
      const response = await bookService.getBookDetail(bookId);
      const bookDetail = response.data.data;

      // 카테고리 정보 설정
      const categoryMajor = bookDetail.category.major;
      const categorySub = bookDetail.category.sub;

      // 해당 메이저 카테고리의 서브 카테고리 목록 업데이트
      const selectedCategory = categories.find(
        (cat) => cat.major === categoryMajor
      );
      if (selectedCategory) {
        setSubCategories(selectedCategory.subs);
      }

      // 폼 데이터 설정
      setBookForm({
        id: bookDetail.id,
        name: bookDetail.name,
        authorName: bookDetail.author.name,
        publisherName: bookDetail.publisher.name,
        categoryMajor: categoryMajor,
        categorySub: categorySub,
        description: bookDetail.description,
        isbn: bookDetail.isbn,
        ecn: bookDetail.ecn || "",
        pubDate: bookDetail.pubDate ? new Date(bookDetail.pubDate) : undefined,
        image: null,
        epubFile: null,
        imageUrl: bookDetail.image,
        publishedDateString: bookDetail.pubDate
          ? new Date(bookDetail.pubDate).toISOString().split("T")[0]
          : "",
      });

      setCoverPreview(bookDetail.image);
      setEditingBookId(bookId);
      setActiveTab("addBook");
    } catch (error) {
      console.error("책 상세 정보 로딩 중 오류 발생:", error);
      toast.error("책 정보를 불러오는 중 오류가 발생했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  // 새 도서 추가 시작
  const handleAddNewBook = () => {
    setBookForm(emptyBookForm);
    setCoverPreview(null);
    setEditingBookId(null);
    setSubCategories([]);
    setActiveTab("addBook");
  };

  // 도서 삭제 확인 창 열기
  const handleDeleteClick = (bookId: number) => {
    setBookToDelete(bookId);
    setDeleteDialogOpen(true);
  };

  // 도서 삭제 확인
  const confirmDelete = async () => {
    if (bookToDelete) {
      try {
        setIsLoading(true);

        // 삭제 API 호출
        await axios.delete(`${API_BASE_URL}/admin/books/${bookToDelete}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
          },
        });

        // 성공 메시지 표시
        toast.success("도서가 성공적으로 삭제되었습니다.");

        // 도서 목록 다시 로드
        await loadBooks(currentPage, selectedCategory);
      } catch (error) {
        console.error("도서 삭제 중 오류 발생:", error);
        toast.error("도서 삭제 중 오류가 발생했습니다. 다시 시도해주세요.");
      } finally {
        setDeleteDialogOpen(false);
        setBookToDelete(null);
        setIsLoading(false);
      }
    }
  };

  // 폼 입력 처리 - 텍스트 필드
  const handleTextChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setBookForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // 폼 입력 처리 - 선택 필드
  const handleSelectChange = (name: string, value: string) => {
    setBookForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // 표지 이미지 업로드 처리
  const handleCoverImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setBookForm((prev) => ({
      ...prev,
      image: file,
    }));

    // 이미지 미리보기 생성
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        setCoverPreview(e.target?.result as string);
      };
      reader.readAsDataURL(file);
    } else {
      // 편집 중이고 기존 이미지가 있는 경우
      if (editingBookId && bookForm.imageUrl) {
        setCoverPreview(bookForm.imageUrl);
      } else {
        setCoverPreview(null);
      }
    }
  };

  // EPUB 파일 업로드 처리
  const handleEpubFileChange = async (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const file = e.target.files?.[0] || null;
    if (!file) return;

    setBookForm((prev) => ({
      ...prev,
      epubFile: file,
    }));

    try {
      // EPUB 파일(ZIP 형식)에서 표지 이미지 추출
      const JSZip = await import("jszip").then((mod) => mod.default);
      const zip = new JSZip();

      // EPUB 파일 읽기
      const epubData = await file.arrayBuffer();
      const contents = await zip.loadAsync(epubData);

      // EPUB 내부 파일 목록 확인
      const files = Object.keys(contents.files);

      // 표지 이미지 파일 찾기 (일반적인 패턴)
      const coverPatterns = [
        /cover\.(jpe?g|png)/i,
        /cover-image\.(jpe?g|png)/i,
        /.*cover.*\.(jpe?g|png)/i,
        /OPS\/images\/cover\.(jpe?g|png)/i,
        /OEBPS\/images\/cover\.(jpe?g|png)/i,
      ];

      let coverFile = null;

      // 표지 이미지 파일 찾기
      for (const pattern of coverPatterns) {
        coverFile = files.find((filename) => pattern.test(filename));
        if (coverFile) break;
      }

      // 표지 이미지가 발견되면 추출
      if (coverFile) {
        const file = contents.file(coverFile);
        if (file) {
          try {
            const imageData = await file.async("blob");
            const imageUrl = URL.createObjectURL(imageData);

            // 표지 이미지 설정
            setCoverPreview(imageUrl);
            setBookForm((prev) => ({
              ...prev,
              image: new File([imageData], "cover.jpg", {
                type: imageData.type,
              }),
            }));

            toast.success("EPUB에서 표지 이미지를 추출했습니다.");
          } catch (error) {
            console.error("표지 이미지 추출 중 오류 발생:", error);
            toast.error("표지 이미지 추출에 실패했습니다.");
          }
        } else {
          console.warn(
            "EPUB에 지정된 표지 파일이 존재하지 않습니다:",
            coverFile
          );
          toast.warning("EPUB에서 표지 이미지를 찾을 수 없습니다.");
        }
      }
    } catch (error) {
      console.error("EPUB 파싱 중 오류:", error);
      toast.error("EPUB 파일 처리 중 오류가 발생했습니다.");
    }
  };

  const handleBookFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    try {
      // 필수 필드 검증
      if (
        !bookForm.name ||
        !bookForm.authorName ||
        !bookForm.publisherName ||
        !bookForm.categorySub ||
        !bookForm.isbn ||
        !bookForm.publishedDateString
      ) {
        toast.error("필수 입력 항목을 모두 입력해주세요.");
        setIsSubmitting(false);
        return;
      }

      let response;

      if (editingBookId) {
        // 도서 수정 API 호출
        const formData = new FormData();

        // 기본 필드 추가
        formData.append("categorySub", bookForm.categorySub);
        formData.append("publisherName", bookForm.publisherName);
        formData.append("authorName", bookForm.authorName);
        formData.append("name", bookForm.name);
        formData.append("description", bookForm.description || "");
        formData.append("isbn", bookForm.isbn);
        formData.append("ecn", bookForm.ecn || "");
        formData.append("pubDate", bookForm.publishedDateString || "");

        // 이미지 파일 추가 (있는 경우에만)
        if (bookForm.image) {
          formData.append("image", bookForm.image);
        }

        // EPUB 파일 추가 (있는 경우에만)
        if (bookForm.epubFile) {
          formData.append("epubFile", bookForm.epubFile);
        }

        response = await axios.put(
          `${API_BASE_URL}/admin/books/${editingBookId}`,
          formData,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
              "Content-Type": "multipart/form-data", // 수정: JSON 대신 multipart/form-data 사용
            },
          }
        );

        toast.success("도서 정보가 성공적으로 수정되었습니다.");
      } else {
        // 도서 추가 API 호출 (FormData 형식)
        const formData = new FormData();

        // 각 필드를 개별적으로 FormData에 추가
        formData.append("categorySub", bookForm.categorySub);
        formData.append("publisherName", bookForm.publisherName);
        formData.append("authorName", bookForm.authorName);
        formData.append("name", bookForm.name);
        formData.append("description", bookForm.description || "");
        formData.append("isbn", bookForm.isbn);
        formData.append("ecn", bookForm.ecn || "");
        formData.append("pubDate", bookForm.publishedDateString || "");

        // 이미지 파일 추가
        if (bookForm.image) {
          formData.append("image", bookForm.image);
        }

        // EPUB 파일 추가
        if (bookForm.epubFile) {
          formData.append("epubFile", bookForm.epubFile);
        }

        // 디버깅을 위해 데이터 출력
        console.log("전송 데이터:");
        console.log("categorySub:", bookForm.categorySub);
        console.log("publisherName:", bookForm.publisherName);
        console.log("authorName:", bookForm.authorName);
        console.log("name:", bookForm.name);
        console.log("description:", bookForm.description);
        console.log("isbn:", bookForm.isbn);
        console.log("ecn:", bookForm.ecn);
        console.log("pubDate:", bookForm.publishedDateString);
        console.log("image:", bookForm.image ? "있음" : "없음");
        console.log("epubFile:", bookForm.epubFile ? "있음" : "없음");

        response = await axios.post(`${API_BASE_URL}/admin/books`, formData, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
            "Content-Type": "multipart/form-data",
          },
        });

        toast.success("새 도서가 성공적으로 추가되었습니다.");
      }

      // 도서 목록 다시 로드
      await loadBooks(currentPage, selectedCategory);

      // 도서 목록 탭으로 이동
      setActiveTab("booksList");
      setEditingBookId(null);
      setBookForm(emptyBookForm);
      setCoverPreview(null);
      setSubCategories([]);
    } catch (error: unknown) {
      const err = error as AxiosError;
      console.error("도서 저장 중 오류:", error);

      // 더 자세한 에러 로깅
      if (err.response) {
        console.error("응답 상태:", err.response.status);
        console.error("응답 데이터:", err.response.data);
        toast.error(`저장 실패: ${err.response.data?.message || "서버 오류"}`);
      } else if (err.request) {
        console.error("요청은 전송됐지만 응답이 없음:", err.request);
        toast.error("서버에서 응답이 없습니다. 네트워크를 확인해주세요.");
      } else {
        console.error("요청 설정 중 오류:", err.message);
        toast.error("요청을 보내는 중 오류가 발생했습니다.");
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  // 도서 폼 취소 처리
  const handleCancelForm = () => {
    setActiveTab("booksList");
    setEditingBookId(null);
    setBookForm(emptyBookForm);
    setCoverPreview(null);
    setSubCategories([]);
  };

  // 페이지네이션 컴포넌트
  const Pagination = () => {
    // 최대 5개 페이지 버튼 표시
    const maxPageButtons = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxPageButtons / 2));
    let endPage = Math.min(totalPages, startPage + maxPageButtons - 1);

    // 표시할 페이지 버튼이 maxPageButtons보다 작으면 startPage 조정
    if (endPage - startPage + 1 < maxPageButtons) {
      startPage = Math.max(1, endPage - maxPageButtons + 1);
    }

    const pageButtons = [];

    // 처음 페이지 버튼
    pageButtons.push(
      <Button
        key="first"
        variant="outline"
        size="sm"
        onClick={() => handlePageChange(1)}
        disabled={currentPage === 1}
        className="hidden sm:inline-flex"
      >
        처음
      </Button>
    );

    // 이전 페이지 버튼
    pageButtons.push(
      <Button
        key="prev"
        variant="outline"
        size="sm"
        onClick={() => handlePageChange(currentPage - 1)}
        disabled={currentPage === 1}
      >
        이전
      </Button>
    );

    // 페이지 번호 버튼
    for (let i = startPage; i <= endPage; i++) {
      pageButtons.push(
        <Button
          key={i}
          variant={currentPage === i ? "default" : "outline"}
          size="sm"
          onClick={() => handlePageChange(i)}
        >
          {i}
        </Button>
      );
    }

    // 다음 페이지 버튼
    pageButtons.push(
      <Button
        key="next"
        variant="outline"
        size="sm"
        onClick={() => handlePageChange(currentPage + 1)}
        disabled={currentPage === totalPages}
      >
        다음
      </Button>
    );

    // 마지막 페이지 버튼
    pageButtons.push(
      <Button
        key="last"
        variant="outline"
        size="sm"
        onClick={() => handlePageChange(totalPages)}
        disabled={currentPage === totalPages}
        className="hidden sm:inline-flex"
      >
        마지막
      </Button>
    );

    return (
      <div className="flex justify-between items-center mt-4">
        <div className="text-sm text-gray-500">
          총 {totalBooks}개의 도서, {currentPage} / {totalPages} 페이지
        </div>
        <div className="flex space-x-2">{pageButtons}</div>
      </div>
    );
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  return (
    <div className="flex flex-col min-h-screen">
      {/* 헤더 */}
      <header className="bg-white shadow-sm z-10 p-4">
        <div className="container mx-auto flex justify-between items-center">
          <div className="flex items-center">
            <BookIcon className="h-6 w-6 text-blue-600 mr-2" />
            <h1 className="text-xl font-bold">도서 관리 시스템</h1>
          </div>
          <div className="flex items-center space-x-3">
            <div className="text-sm text-gray-600 mr-2">
              {admin?.name || "관리자"} ({admin?.email || ""})
            </div>
            <Button
              variant="outline"
              size="sm"
              className="text-red-600"
              onClick={handleLogout}
            >
              <LogOutIcon className="h-4 w-4 mr-2" />
              로그아웃
            </Button>
          </div>
        </div>
      </header>

      {/* 메인 컨텐츠 */}
      <main className="flex-1 container mx-auto pt-6 pb-10">
        <Tabs
          value={activeTab}
          onValueChange={setActiveTab}
          className="space-y-6"
        >
          <TabsList className="grid grid-cols-2 w-full max-w-md">
            <TabsTrigger value="booksList">도서 목록</TabsTrigger>
            <TabsTrigger value="addBook">도서 추가</TabsTrigger>
          </TabsList>

          {/* 도서 추가 및 편집 탭 */}
          <TabsContent value="addBook" className="space-y-6">
            <div className="flex justify-between">
              <div>
                <h2 className="text-xl font-bold mb-2">
                  {editingBookId ? "도서 정보 수정" : "새 도서 추가"}
                </h2>
                <p className="text-gray-500">
                  {editingBookId
                    ? "기존 도서 정보를 수정합니다."
                    : "새로운 도서 정보를 입력하여 추가합니다."}
                </p>
              </div>
              <div className="flex justify-end mt-6 space-x-3">
                <Button
                  type="button"
                  variant="outline"
                  onClick={handleCancelForm}
                  disabled={isSubmitting}
                >
                  취소
                </Button>
                <Button
                  type="submit"
                  form="bookForm" // form ID 연결
                  className="bg-blue-600 hover:bg-blue-700"
                  disabled={isSubmitting}
                >
                  {isSubmitting ? (
                    <>
                      <div className="animate-spin mr-2 h-4 w-4 border-2 border-b-transparent rounded-full" />
                      처리 중...
                    </>
                  ) : (
                    <>
                      <SaveIcon className="mr-2 h-4 w-4" />
                      {editingBookId ? "도서 수정" : "도서 저장"}
                    </>
                  )}
                </Button>
              </div>
            </div>

            <form id="bookForm" onSubmit={handleBookFormSubmit}>
              <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* 이미지 및 파일 업로드 섹션 */}
                <Card className="lg:col-span-1">
                  <CardHeader>
                    <CardTitle>도서 이미지 및 파일</CardTitle>
                    <CardDescription>
                      책 표지 이미지와 EPUB 파일을 업로드합니다.
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-6">
                    {/* 표지 이미지 업로드 */}
                    <div className="space-y-3">
                      <Label htmlFor="image">표지 이미지 (필수)</Label>
                      <div
                        className={cn(
                          "border-2 border-dashed rounded-lg p-4 text-center hover:bg-gray-50 transition cursor-pointer",
                          coverPreview
                            ? "border-transparent"
                            : "border-gray-300"
                        )}
                        onClick={() =>
                          document.getElementById("image")?.click()
                        }
                      >
                        {coverPreview ? (
                          <div className="relative">
                            <img
                              src={coverPreview}
                              alt="Preview"
                              className="max-h-64 mx-auto rounded-md"
                            />
                            <button
                              type="button"
                              className="absolute top-2 right-2 bg-white rounded-full p-1 shadow-md"
                              onClick={(e) => {
                                e.stopPropagation();
                                setCoverPreview(null);
                                setBookForm((prev) => ({
                                  ...prev,
                                  image: null,
                                  imageUrl: undefined,
                                }));
                              }}
                            >
                              <svg
                                xmlns="http://www.w3.org/2000/svg"
                                width="16"
                                height="16"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                strokeWidth="2"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                              >
                                <line x1="18" y1="6" x2="6" y2="18"></line>
                                <line x1="6" y1="6" x2="18" y2="18"></line>
                              </svg>
                            </button>
                          </div>
                        ) : (
                          <div className="py-6">
                            <UploadIcon className="h-10 w-10 text-gray-400 mx-auto mb-2" />
                            <p className="text-sm text-gray-500">
                              클릭하여 표지 이미지 업로드
                            </p>
                            <p className="text-xs text-gray-400 mt-1">
                              JPG, PNG 파일 (최대 5MB)
                            </p>
                          </div>
                        )}
                        <input
                          type="file"
                          id="image"
                          name="image"
                          accept="image/jpeg, image/png"
                          className="hidden"
                          onChange={handleCoverImageChange}
                        />
                      </div>
                    </div>

                    {/* EPUB 파일 업로드 */}
                    <div className="space-y-3">
                      <Label htmlFor="epubFile">
                        EPUB 파일 {!editingBookId && "(필수)"}
                      </Label>
                      <div
                        className="border-2 border-dashed border-gray-300 rounded-lg p-4 text-center hover:bg-gray-50 transition cursor-pointer"
                        onClick={() =>
                          document.getElementById("epubFile")?.click()
                        }
                      >
                        {bookForm.epubFile ? (
                          <div className="p-2 bg-gray-100 rounded-md flex items-center justify-between">
                            <div className="text-sm truncate">
                              {bookForm.epubFile.name}
                            </div>
                            <button
                              type="button"
                              className="text-gray-500 hover:text-gray-700"
                              onClick={(e) => {
                                e.stopPropagation();
                                setBookForm((prev) => ({
                                  ...prev,
                                  epubFile: null,
                                }));
                              }}
                            >
                              <svg
                                xmlns="http://www.w3.org/2000/svg"
                                width="16"
                                height="16"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                strokeWidth="2"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                              >
                                <line x1="18" y1="6" x2="6" y2="18"></line>
                                <line x1="6" y1="6" x2="18" y2="18"></line>
                              </svg>
                            </button>
                          </div>
                        ) : (
                          <div className="py-6">
                            <BookIcon className="h-10 w-10 text-gray-400 mx-auto mb-2" />
                            <p className="text-sm text-gray-500">
                              클릭하여 EPUB 파일 업로드
                            </p>
                            <p className="text-xs text-gray-400 mt-1">
                              EPUB 파일 (최대 50MB)
                            </p>
                            {editingBookId && (
                              <p className="text-xs text-orange-500 mt-2">
                                * 수정 시 파일 변경이 없으면 기존 파일이
                                유지됩니다.
                              </p>
                            )}
                          </div>
                        )}
                        <input
                          type="file"
                          id="epubFile"
                          name="epubFile"
                          accept=".epub"
                          className="hidden"
                          onChange={handleEpubFileChange}
                          required={!editingBookId}
                        />
                      </div>
                    </div>
                  </CardContent>
                </Card>

                {/* 도서 기본 정보 섹션 */}
                <Card className="lg:col-span-2">
                  <CardHeader>
                    <CardTitle>도서 기본 정보</CardTitle>
                    <CardDescription>
                      도서의 기본 정보를 입력합니다.
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-6">
                    {/* 도서명 */}
                    <div className="space-y-2">
                      <Label htmlFor="name">도서명 (필수)</Label>
                      <Input
                        id="name"
                        name="name"
                        placeholder="도서명을 입력하세요"
                        value={bookForm.name}
                        onChange={handleTextChange}
                        required
                      />
                    </div>

                    {/* 저자 */}
                    <div className="space-y-2">
                      <Label htmlFor="authorName">저자 (필수)</Label>
                      <Input
                        id="authorName"
                        name="authorName"
                        placeholder="저자명을 입력하세요"
                        value={bookForm.authorName}
                        onChange={handleTextChange}
                        required
                      />
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="publisherName">출판사 (필수)</Label>
                      <Select
                        value={bookForm.publisherName || ""}
                        onValueChange={handlePublisherChange}
                      >
                        <SelectTrigger id="publisherName" className="w-full">
                          <SelectValue placeholder="출판사 선택" />
                        </SelectTrigger>
                        <SelectContent>
                          {publishers.map((publisher) => (
                            <SelectItem
                              key={publisher.id}
                              value={
                                publisher.name || `publisher-${publisher.id}`
                              }
                            >
                              {publisher.name || "이름 없음"}
                            </SelectItem>
                          ))}
                          <SelectItem value="직접입력">
                            <span className="flex items-center text-blue-600">
                              <svg
                                xmlns="http://www.w3.org/2000/svg"
                                width="16"
                                height="16"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                strokeWidth="2"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                className="mr-1"
                              >
                                <line x1="12" y1="5" x2="12" y2="19"></line>
                                <line x1="5" y1="12" x2="19" y2="12"></line>
                              </svg>
                              새 출판사 추가
                            </span>
                          </SelectItem>
                        </SelectContent>
                      </Select>
                    </div>

                    {/* 카테고리 */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label htmlFor="categoryMajor">대분류 (필수)</Label>
                        <Select
                          value={bookForm.categoryMajor || "none"}
                          onValueChange={handleCategoryMajorChange}
                        >
                          <SelectTrigger id="categoryMajor">
                            <SelectValue placeholder="대분류 선택" />
                          </SelectTrigger>
                          <SelectContent>
                            {categories.map((category) => (
                              <SelectItem
                                key={category.major || `major-${category.id}`}
                                value={category.major || `major-${category.id}`}
                              >
                                {category.major || `카테고리 #${category.id}`}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </div>

                      <div className="space-y-2">
                        <Label htmlFor="categorySub">소분류 (필수)</Label>
                        <Select
                          value={bookForm.categorySub || "none"}
                          onValueChange={(value) =>
                            handleSelectChange("categorySub", value)
                          }
                          disabled={
                            !bookForm.categoryMajor ||
                            bookForm.categoryMajor === "none"
                          }
                        >
                          <SelectTrigger id="categorySub">
                            <SelectValue placeholder="소분류 선택" />
                          </SelectTrigger>
                          <SelectContent>
                            {subCategories.length > 0 ? (
                              subCategories.map((subCategory, index) => (
                                <SelectItem
                                  key={subCategory || `sub-${index}`}
                                  value={subCategory || `sub-${index}`}
                                >
                                  {subCategory || `소분류 #${index + 1}`}
                                </SelectItem>
                              ))
                            ) : (
                              <SelectItem value="none">
                                대분류를 먼저 선택하세요
                              </SelectItem>
                            )}
                          </SelectContent>
                        </Select>
                      </div>
                    </div>

                    {/* ISBN 및 ECN */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label htmlFor="isbn">ISBN (필수)</Label>
                        <Input
                          id="isbn"
                          name="isbn"
                          placeholder="ISBN 번호 (예: 9788901234567)"
                          value={bookForm.isbn}
                          onChange={handleTextChange}
                          required
                        />
                      </div>

                      <div className="space-y-2">
                        <Label htmlFor="ecn">ECN (선택)</Label>
                        <Input
                          id="ecn"
                          name="ecn"
                          placeholder="ECN 번호 (있는 경우)"
                          value={bookForm.ecn}
                          onChange={handleTextChange}
                        />
                      </div>
                    </div>

                    {/* 출판일 */}
                    <div className="space-y-2">
                      <div className="flex items-center justify-between">
                        <Label htmlFor="publishedDateString">
                          출판일 (필수)
                        </Label>
                      </div>
                      <div>
                        <Input
                          id="publishedDateString"
                          name="publishedDateString"
                          placeholder="YYYY-MM-DD"
                          value={bookForm.publishedDateString}
                          onChange={handleDateStringChange}
                          required
                        />
                        {dateInputError && (
                          <p className="text-red-500 text-xs mt-1">
                            {dateInputError}
                          </p>
                        )}
                      </div>
                    </div>

                    {/* 책 설명 */}
                    <div className="space-y-2">
                      <Label htmlFor="description">도서 설명</Label>
                      <Textarea
                        id="description"
                        name="description"
                        placeholder="도서 설명을 입력하세요"
                        value={bookForm.description}
                        onChange={handleTextChange}
                        rows={6}
                      />
                    </div>
                  </CardContent>
                </Card>
              </div>
            </form>
          </TabsContent>

          {/* 도서 목록 탭 */}
          <TabsContent value="booksList" className="space-y-6">
            <div className="flex justify-between items-center">
              <h2 className="text-xl font-bold">도서 목록 관리</h2>
              <Button
                className="bg-blue-600 hover:bg-blue-700"
                onClick={handleAddNewBook}
              >
                <BookIcon className="h-4 w-4 mr-2" />새 도서 추가
              </Button>
            </div>

            <div className="flex flex-col md:flex-row space-y-4 md:space-y-0 md:space-x-4 mb-4">
              <div className="flex-1 relative">
                <Input
                  placeholder="도서명 또는 저자로 검색"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  onKeyPress={handleSearchKeyPress}
                  className="pr-10"
                />
                <Button
                  variant="ghost"
                  size="sm"
                  className="absolute right-0 top-0 h-full"
                  onClick={handleSearch}
                >
                  <SearchIcon className="h-4 w-4" />
                </Button>
              </div>
              <div className="w-full md:w-52">
                <Select
                  value={selectedCategory || "null"}
                  onValueChange={handleCategoryChange}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="카테고리 선택" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="null">전체 카테고리</SelectItem>
                    {categories.map((category) => (
                      <SelectItem
                        key={category.major || `major-${category.id}`}
                        value={category.major || `major-${category.id}`}
                      >
                        {category.major || `카테고리 #${category.id}`}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>

            <Card>
              <CardContent className="px-6 py-4">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead className="w-12">표지</TableHead>
                      <TableHead>도서명</TableHead>
                      <TableHead>저자</TableHead>
                      <TableHead>출판사</TableHead>
                      <TableHead>카테고리</TableHead>
                      <TableHead>ISBN</TableHead>
                      <TableHead>출판일</TableHead>
                      <TableHead className="text-right">관리</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {books.length === 0 ? (
                      <TableRow>
                        <TableCell
                          colSpan={8}
                          className="text-center py-6 text-gray-500"
                        >
                          검색 결과가 없습니다.
                        </TableCell>
                      </TableRow>
                    ) : (
                      books.map((book) => (
                        <TableRow key={book.id}>
                          <TableCell>
                            {book.image ? (
                              <img
                                src={book.image}
                                alt={book.name}
                                className="w-10 h-14 object-cover rounded"
                              />
                            ) : (
                              <div className="w-10 h-14 bg-gray-200 flex items-center justify-center rounded">
                                <BookIcon className="h-6 w-6 text-gray-400" />
                              </div>
                            )}
                          </TableCell>
                          <TableCell className="font-medium">
                            {book.name || "제목 없음"}
                          </TableCell>
                          <TableCell>
                            {book.author?.name || book.authorName || "-"}
                          </TableCell>
                          <TableCell>{book.publisher?.name || "-"}</TableCell>
                          <TableCell>
                            <span className="px-2 py-1 bg-gray-100 text-gray-800 rounded-full text-xs">
                              {book.category?.major ||
                                book.categoryMajor ||
                                "분류 없음"}
                            </span>{" "}
                            <span className="text-xs text-gray-600">
                              {book.category?.sub || book.categorySub || ""}
                            </span>
                          </TableCell>
                          <TableCell className="font-mono text-xs">
                            {book.isbn || "-"}
                          </TableCell>
                          <TableCell>
                            {book.pubDate
                              ? new Date(book.pubDate).toLocaleDateString()
                              : "-"}
                          </TableCell>
                          <TableCell className="text-right">
                            <div className="flex justify-end space-x-2">
                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => handleEditBook(book.id)}
                              >
                                <PencilIcon className="h-4 w-4" />
                              </Button>
                              <Button
                                variant="outline"
                                size="sm"
                                className="text-red-600 border-red-200 hover:bg-red-50"
                                onClick={() => handleDeleteClick(book.id)}
                              >
                                <TrashIcon className="h-4 w-4" />
                              </Button>
                            </div>
                          </TableCell>
                        </TableRow>
                      ))
                    )}
                  </TableBody>
                </Table>

                {/* 페이지네이션 */}
                {books.length > 0 && <Pagination />}
              </CardContent>
            </Card>

            {/* 도서 삭제 확인 다이얼로그 */}
            <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>도서 삭제 확인</DialogTitle>
                  <DialogDescription>
                    정말로 이 도서를 삭제하시겠습니까? 이 작업은 되돌릴 수
                    없습니다.
                  </DialogDescription>
                </DialogHeader>
                <DialogFooter>
                  <Button
                    variant="outline"
                    onClick={() => setDeleteDialogOpen(false)}
                  >
                    취소
                  </Button>
                  <Button
                    variant="destructive"
                    onClick={confirmDelete}
                    className="bg-red-600 hover:bg-red-700"
                  >
                    삭제
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          </TabsContent>
        </Tabs>
      </main>
      {/* 출판사 추가 모달 다이얼로그 */}
      <Dialog open={publisherModalOpen} onOpenChange={setPublisherModalOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>새 출판사 추가</DialogTitle>
            <DialogDescription>
              추가하실 출판사 이름을 입력하세요.
            </DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <div className="space-y-2">
              <Label htmlFor="newPublisherName" className="font-medium">
                출판사명
              </Label>
              <Input
                id="newPublisherName"
                value={newPublisherName}
                onChange={(e) => setNewPublisherName(e.target.value)}
                placeholder="출판사 이름을 입력하세요"
                className="w-full"
                autoFocus
              />
            </div>
          </div>
          <DialogFooter className="sm:justify-between">
            <Button
              variant="outline"
              onClick={() => setPublisherModalOpen(false)}
              disabled={isAddingPublisher}
            >
              취소
            </Button>
            <Button
              type="button"
              onClick={handleAddPublisher}
              disabled={!newPublisherName.trim() || isAddingPublisher}
              className="bg-blue-600 hover:bg-blue-700"
            >
              {isAddingPublisher ? (
                <>
                  <div className="animate-spin mr-2 h-4 w-4 border-2 border-b-transparent rounded-full" />
                  추가 중...
                </>
              ) : (
                <>추가하기</>
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default AdminDashboard;
