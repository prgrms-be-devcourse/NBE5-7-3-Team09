import React, { useState } from "react";
import { ChevronDown, ChevronUp, Search } from "lucide-react";

// FAQ 카테고리 데이터
const faqCategories = [
  { id: "all", name: "전체" },
  { id: "account", name: "계정" },
  { id: "payment", name: "결제" },
  { id: "service", name: "서비스 이용" },
  { id: "content", name: "콘텐츠" },
  { id: "technical", name: "기술 지원" },
];

// FAQ 데이터 (가상)
const faqData = [
  {
    id: 1,
    category: "account",
    question: "비밀번호를 잊어버렸어요.",
    answer: "비밀번호 분실시, 재설정 기능은 현재 기능 개발중입니다. 비밀번호 분실시 재가입 후, 팀 9구글에 포인트이전으로 문의부탁드립니다.",
    isPopular: true,
  },
  {
    id: 2,
    category: "account",
    question: "닉네임을 변경하고 싶어요.",
    answer: "마이페이지 > 회원정보 > 수정에서 닉네임을 변경할 수 있습니다. 수정하신 닉네임으로 Readio 사용이름이 변경됩니다.",
    isPopular: true,
  },
  {
    id: 3,
    category: "payment",
    question: "결제 방법은 어떤 것이 있나요?",
    answer: "현재는 토스페이로만 결제시스템을 제공하고 있습니다. 추후 결제 서비스 업데이트는 공지사항을 참조해주세요.",
    isPopular: true,
  },
  {
    id: 4,
    category: "payment",
    question: "환불은 어떻게 신청하나요?",
    answer: "이미 구독권을 구매하신 경우, 환불은 어렵습니다. 다만, 구독해지는 가능합니다. 구독해지 후에도 콘텐츠는 구독만료일까지 사용가능합니다.",
    isPopular: true,
  },
  {
    id: 5,
    category: "service",
    question: "여러 기기에서 동시에 이용할 수 있나요?",
    answer: "한 계정으로 최대 5대의 기기에서 이용할 수 있습니다. 단, 동시에 서로 다른 기기에서 접속할 경우 일부 기능이 제한될 수 있습니다.",
    isPopular: false,
  },
  {
    id: 6,
    category: "service",
    question: "포인트는 어떻게 사용하나요?",
    answer: "마이페이지 > 구독현황 > 구독 시작하기 버튼을 누르면 자동으로 포인트가 차감이 되어 결제가 됩니다. 보유하신 포인트 내역은 마이페이지 > 회원정보에서 확인할 수 있습니다.",
    isPopular: true,
  },
  {
    id: 7,
    category: "content",
    question: "구독권구매 후 책 어디서 볼 수 있나요?",
    answer: "책은 '라이브러리'에서 확인하실 수 있습니다. 웹사이트에서 '라이브러리' 메뉴를 통해 접근할 수 있으며, 다운로드하여 오프라인에서도 이용 가능합니다.",
    isPopular: false,
  },
  {
    id: 8,
    category: "content",
    question: "콘텐츠 오류는 어디에 신고하나요?",
    answer: "콘텐츠 내에 오류를 발견하셨다면, 사이트 하단의 팀 9글로 문의주시면 빠른 오류수정이 가능합니다.",
    isPopular: false,
  },
  {//todo
    id: 9,
    category: "technical",
    question: "뷰어가 안열려요",
    answer: "뷰어의 문제가 있다면 해당 도서를 라이브러리에서 삭제 후 다시 추가해서 열어보세요. 그래도 작동하지 않는다면 팀 9글로 연락부탁드립니다.",
    isPopular: false,
  },
  {
    id: 10,
    category: "technical",
    question: "기기 변경 시 콘텐츠를 이전할 수 있나요?",
    answer: "네, 동일한 계정으로 로그인하시면 구매하신 모든 콘텐츠를 새 기기에서도 이용하실 수 있습니다. 새 기기에서 웹사이트 방문 후 로그인하여 '내 서재'를 확인해 주세요.",
    isPopular: true,
  },
  {
    id: 11,
    category: "account",
    question: "전화번호를 변경하고 싶어요.",
    answer: "마이페이지 > 회원정보 > 수정에서 전화번호를 변경할 수 있습니다.",
    isPopular: false,
  },
  {
    id: 12,
    category: "payment",
    question: "결제 수단을 변경하고 싶어요.",
    answer: "마이페이지 > 설정 > 결제 수단 관리에서 변경 가능합니다. 신규 결제 수단을 등록하거나 기존 결제 수단을 삭제할 수 있으며, 기본 결제 수단을 지정할 수도 있습니다.",
    isPopular: false,
  },
  {
    id: 13,
    category: "service",
    question: "구독확인 메일이 안와요",
    answer: "메일전송량이 많을 시에 일시적으로 메일 발송에 시간이 걸릴 수 있으니, 10분정도가 소요될 수 있습니다. 빠른 확인을 원하시면 마이페이지 > 구독현황에서 회원님의 구독현황을 확인하실 수 있습니다.",
    isPopular: true,
  },
];

const FAQ = () => {
  const [selectedCategory, setSelectedCategory] = useState("all");
  const [expandedQuestions, setExpandedQuestions] = useState<number[]>([]);
  const [searchTerm, setSearchTerm] = useState("");

  // 검색어와 카테고리로 FAQ 필터링
  const filteredFAQs = faqData.filter((faq) => {
    const matchesCategory = selectedCategory === "all" || faq.category === selectedCategory;
    const matchesSearch = searchTerm === "" || 
      faq.question.toLowerCase().includes(searchTerm.toLowerCase()) || 
      faq.answer.toLowerCase().includes(searchTerm.toLowerCase());
    
    return matchesCategory && matchesSearch;
  });

  // 인기 질문만 필터링
  const popularFAQs = faqData.filter(faq => faq.isPopular);

  // 질문 토글 핸들러
  const toggleQuestion = (id: number) => {
    setExpandedQuestions(prev => 
      prev.includes(id) 
        ? prev.filter(item => item !== id) 
        : [...prev, id]
    );
  };

  // 검색 핸들러
  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  // 카테고리 변경 핸들러
  const handleCategoryChange = (categoryId: string) => {
    setSelectedCategory(categoryId);
    setExpandedQuestions([]);
  };

  return (
    <div className="container mx-auto px-4 py-8">
      {/* 헤더 */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold mb-2">자주 묻는 질문</h1>
        <p className="text-gray-500">궁금한 점이 있으신가요? 자주 묻는 질문에서 해결책을 찾아보세요.</p>
      </div>

      {/* 검색 바 */}
      <div className="mb-8">
        <div className="relative">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Search className="h-5 w-5 text-gray-400" />
          </div>
          <input
            type="text"
            className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
            placeholder="질문 검색하기"
            value={searchTerm}
            onChange={handleSearch}
          />
        </div>
      </div>

      {/* 인기 질문 섹션 (검색어 없을 때만 표시) */}
      {searchTerm === "" && selectedCategory === "all" && (
        <div className="mb-8">
          <h2 className="text-lg font-semibold mb-4">인기 질문</h2>
          <div className="bg-gray-50 rounded-lg p-4">
            {popularFAQs.map((faq) => (
              <div key={faq.id} className="mb-4 last:mb-0">
                <button
                  className="flex justify-between items-start w-full text-left font-medium text-gray-900 hover:text-blue-600"
                  onClick={() => toggleQuestion(faq.id)}
                >
                  <span>{faq.question}</span>
                  {expandedQuestions.includes(faq.id) ? (
                    <ChevronUp className="h-5 w-5 text-gray-500" />
                  ) : (
                    <ChevronDown className="h-5 w-5 text-gray-500" />
                  )}
                </button>
                {expandedQuestions.includes(faq.id) && (
                  <div className="mt-2 text-gray-600 text-sm pl-1">
                    {faq.answer}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* 카테고리 탭 */}
      <div className="mb-6 overflow-x-auto">
        <div className="flex border-b">
          {faqCategories.map((category) => (
            <button
              key={category.id}
              className={`px-4 py-2 text-sm whitespace-nowrap ${
                selectedCategory === category.id
                  ? "text-blue-600 border-b-2 border-blue-600 font-medium"
                  : "text-gray-600 hover:text-blue-600"
              }`}
              onClick={() => handleCategoryChange(category.id)}
            >
              {category.name}
            </button>
          ))}
        </div>
      </div>

      {/* FAQ 아코디언 */}
      <div className="border rounded-md divide-y">
        {filteredFAQs.length > 0 ? (
          filteredFAQs.map((faq) => (
            <div key={faq.id} className="p-4">
              <button
                className="flex justify-between items-center w-full text-left font-medium hover:text-blue-600"
                onClick={() => toggleQuestion(faq.id)}
              >
                <span>{faq.question}</span>
                {expandedQuestions.includes(faq.id) ? (
                  <ChevronUp className="h-5 w-5 text-gray-500" />
                ) : (
                  <ChevronDown className="h-5 w-5 text-gray-500" />
                )}
              </button>
              {expandedQuestions.includes(faq.id) && (
                <div className="mt-2 text-gray-600">
                  {faq.answer}
                </div>
              )}
            </div>
          ))
        ) : (
          <div className="p-8 text-center text-gray-500">
            검색 결과가 없습니다. 다른 검색어를 입력하거나 카테고리를 변경해 보세요.
          </div>
        )}
      </div>

      {/* 추가 문의 */}
      <div className="mt-8 p-6 bg-gray-50 rounded-lg text-center">
        <h3 className="text-lg font-semibold mb-2">원하는 답변을 찾지 못하셨나요?</h3>
        <p className="text-gray-600 mb-4">
          고객센터에 문의하시면 빠르게 답변해 드리겠습니다.
        </p>
        <button className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors">
          1:1 문의하기
        </button>
      </div>
    </div>

  );
};

export default FAQ;