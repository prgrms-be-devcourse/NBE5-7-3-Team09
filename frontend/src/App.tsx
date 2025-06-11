// 간소화된 App.tsx 라우터 구성
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Layout from "./components/layout/Layout";
import MainPage from "./pages/MainPage";
import LoginPage from "./pages/member/LoginPage";
import SignupPage from "./pages/member/SignupPage";
import { AuthProvider } from "./contexts/AuthContext";
import BookDetailPage from "./pages/book/BookDetailPage";
import BookReviewsPage from "./pages/book/BookReviewsPage";
import EpubReaderPage from "./pages/book/EpubReaderPage";
import BooksPage from "./pages/book/BooksPage"; // 도서 목록 페이지
import CategoryRedirect from "./components/book/CategoryRedirect";
import LibraryPage from "./pages/library/LibraryPage";
import LibraryDetailPage from "./pages/library/LibraryDetailPage";
import { Toaster } from "sonner";
import MyPage from "./pages/member/MyPage";
import FAQ from "./components/cs/faq";
import NoticeBoard from "./components/cs/notice";

// 관리자 페이지 및 인증 관련 컴포넌트
import AdminLoginPage from "./pages/admin/AdminLoginPage";
import AdminDashboard from "./pages/admin/AdminDashboard";
import AdminProtectedRoute from "./components/admin/AdminProtectedRoute";
import { AdminAuthProvider } from "./contexts/AdminAuthContext";
import PreferencePage from "./pages/member/PreferencePage";

// Toss 결제 관련
import CheckoutPage from "./pages/payment/CheckoutPage";
import SuccessPage from "./pages/payment/SuccessPage";
import FailPage from "./pages/payment/FailPage";

function App() {
  return (
    <Router>
      <AuthProvider>
        <AdminAuthProvider>
          <Toaster position="top-right" />
          <Routes>
            {/* 관리자 라우트 */}
            <Route path="/admin/login" element={<AdminLoginPage />} />
            <Route element={<AdminProtectedRoute />}>
              <Route path="/admin" element={<AdminDashboard />} />
            </Route>

            {/* EPUB 리더 (전체 화면) */}
            <Route path="/read/:id" element={<EpubReaderPage />} />

            {/* 일반 사용자 라우트 (Layout 포함) */}
            <Route
              path="*"
              element={
                <Layout>
                  <Routes>
                    <Route path="/" element={<MainPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/signup" element={<SignupPage />} />

                    {/* Toss 결제 */}
                    <Route path="/checkout" element={<CheckoutPage />} />
                    <Route path="/success" element={<SuccessPage />} />
                    <Route path="/fail" element={<FailPage />} />

                    {/* 도서 관련 라우트 */}
                    <Route path="/books" element={<BooksPage />} />
                    <Route path="/book/:id" element={<BookDetailPage />} />
                    <Route
                      path="/book/:id/reviews"
                      element={<BookReviewsPage />}
                    />

                    {/* 카테고리 관련 리다이렉트 (기존 URL 호환성 유지) */}
                    <Route
                      path="/category/:id"
                      element={<CategoryRedirect />}
                    />
                    <Route path="/all" element={<CategoryRedirect />} />

                    {/* 라이브러리 관련 라우트 */}
                    <Route path="/library" element={<LibraryPage />} />
                    <Route
                      path="/library/:id"
                      element={<LibraryDetailPage />}
                    />

                    {/* 유저 관련 라우트 */}
                    <Route path="/my-page" element={<MyPage />} />
                    <Route path="/preference" element={<PreferencePage />} />

                    {/* CS관련 라우트 추가 */}
                    <Route path="/cs/faq" element={<FAQ />} />
                    <Route path="/cs/notice" element={<NoticeBoard />} />

                  </Routes>
                </Layout>
              }
            />
          </Routes>
        </AdminAuthProvider>
      </AuthProvider>
    </Router>
  );
}

export default App;
