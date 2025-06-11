import React from "react";
import { ChevronRight } from "lucide-react";

const notices = [
  {
    id: 1,
    title: "[안내] 서비스 점검 안내 (5/22)",
    date: "2025-05-16",
    category: "서비스 점검",
    isImportant: true,
    content: "5월 22일 00시부터 06시까지 서비스 점검이 예정되어 있습니다. 점검 시간 동안 서비스가 일시 중단될 수 있으니 양해 부탁드립니다.",
  },
  {
    id: 2,
    title: "[안내] 개인정보처리방침 개정 안내",
    date: "2025-05-01",
    category: "정책 안내",
    isImportant: true,
    content: "2025년 5월 1일부터 개인정보처리방침이 개정되어 적용됩니다. 변경된 내용은 홈페이지 내 개인정보처리방침 페이지에서 확인 가능합니다.",
  },
  {
    id: 3,
    title: "[이벤트] 여름맞이 특별 이벤트 안내",
    date: "2025-05-15",
    category: "이벤트",
    isImportant: false,
    content: "여름맞이 특별 이벤트가 5월 20일부터 6월 10일까지 진행됩니다. 이벤트 기간 동안 다양한 혜택과 선물이 준비되어 있으니 많은 참여 부탁드립니다.",
  },
  {
    id: 4,
    title: "[안내] 결제 시스템 개선 안내",
    date: "2025-05-19",
    category: "서비스 개선",
    isImportant: false,
    content: "더 나은 결제 경험을 제공하기 위해 토스페이 결제 시스템이 추가되었습니다. 이제 포인트 결제를 토스페이를 통해서 하실 수 있습니다. 포인트 충전 결제시, 결제 처리 속도가 향상되었으며, 일부 UI가 변경되었으니 참고해 주세요.",
  },
  {
    id: 5,
    title: "[안내] 6월 휴무 안내",
    date: "2025-05-23",
    category: "휴무 안내",
    isImportant: false,
    content: "2025년 6월 6일 현충일은 휴무일입니다. 6월 부터는 토요일도 휴무일로 변경이 됩니다. 현충일과 주말은 고객센터 운영이 제한되니 문의는 휴무 전후로 부탁드립니다.",
  },
  {
    id: 6,
    title: "[업데이트] 웹사이트 디자인 개편 안내",
    date: "2025-05-10",
    category: "업데이트",
    isImportant: false,
    content: "웹사이트 디자인이 최신 트렌드에 맞게 개편되었습니다. 사용자 편의성을 높이고, 웹사이트 환경 최적화를 강화하였습니다.",
  },
  {
    id: 7,
    title: "[안내] 고객센터 운영시간 변경 안내",
    date: "2025-05-19",
    category: "고객센터",
    isImportant: false,
    content: "고객센터 운영시간이 6월 1일부터 평일 오전 9시부터 오후 6시까지로 변경됩니다. 토요일 및 공휴일은 휴무입니다.",
  },
  {
    id: 8,
    title: "[장애] 서비스 접속 장애 안내 (해결)",
    date: "2025-05-15",
    category: "장애 안내",
    isImportant: false,
    content: "5월 15일 오전 10시부터 약 1시간 동안 서비스 접속 장애가 발생했습니다. 현재 문제는 해결되었으며, 이용에 불편을 드려 죄송합니다.",
  },
  {
    id: 9,
    title: "[안내] 신규 서비스 출시 안내",
    date: "2025-05-10",
    category: "서비스 안내",
    isImportant: false,
    content: "새로운 전자책 추천 서비스가 출시되었습니다. 사용자분들의 다양한 이용부탁드립니다.",
  },
  {
    id: 10,
    title: "[이벤트] 신규가입시 50,000포인트 증정 이벤트",
    date: "2025-05-16",
    category: "이벤트",
    isImportant: false,
    content: "5월 19일부터 5월 23일까지 신규가입시에 50,000포인트를 제공하고있습니다. 서비스에 많은 관심부탁드립니다.",
  },
];


const NoticeBoard = () => {
  const [selectedCategory, setSelectedCategory] = React.useState("all");
  const [expandedId, setExpandedId] = React.useState(null);

  const filteredNotices = selectedCategory === "all" 
    ? notices 
    : notices.filter(notice => {
        if (selectedCategory === "service") return notice.category.includes("서비스");
        if (selectedCategory === "update") return notice.category.includes("업데이트");
        if (selectedCategory === "event") return notice.category.includes("이벤트");
        if (selectedCategory === "policy") return notice.category.includes("정책");
        if (selectedCategory === "maintenance") return notice.category.includes("점검");
        return true;
      });

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
  };

  const toggleExpand = (id) => {
    setExpandedId(expandedId === id ? null : id);
  };

  return (
    <div className="container mx-auto px-4 py-8">
      {/* 카테고리 탭 */}
      <div className="mb-6 overflow-x-auto">
        <div className="flex border-b">
          {[
            { id: "all", name: "전체" },
            { id: "service", name: "서비스 안내" },
            { id: "update", name: "업데이트" },
            { id: "event", name: "이벤트" },
            { id: "policy", name: "정책 안내" },
            { id: "maintenance", name: "점검 안내" },
          ].map((category) => (
            <button
              key={category.id}
              className={`px-4 py-2 text-sm whitespace-nowrap ${
                selectedCategory === category.id
                  ? "text-blue-600 border-b-2 border-blue-600 font-medium"
                  : "text-gray-600 hover:text-blue-600"
              }`}
              onClick={() => {
                setSelectedCategory(category.id);
                setExpandedId(null);
              }}
            >
              {category.name}
            </button>
          ))}
        </div>
      </div>

      {/* 공지사항 리스트 */}
      <div className="border rounded-md divide-y">
        {filteredNotices.map((notice) => (
          <div key={notice.id} className="cursor-pointer" onClick={() => toggleExpand(notice.id)}>
            <div className={`flex justify-between items-center p-4 ${notice.isImportant ? "bg-blue-50 hover:bg-blue-100" : "hover:bg-gray-50"}`}>
              <div className="flex flex-col sm:flex-row sm:items-center w-full">
                {notice.isImportant && (
                  <span className="inline-block px-2 py-1 text-xs font-medium bg-red-100 text-red-800 rounded mr-3">
                    중요
                  </span>
                )}
                <span className="text-sm text-gray-500 w-24 mb-2 sm:mb-0">{formatDate(notice.date)}</span>
                <span className="flex-grow sm:ml-4 font-medium">{notice.title}</span>
              </div>
              <ChevronRight
                className={`w-4 h-4 text-gray-400 flex-shrink-0 transform transition-transform duration-200 ${
                  expandedId === notice.id ? "rotate-90" : ""
                }`}
              />
            </div>
            {expandedId === notice.id && (
                <div
                    className="px-6 pb-4 text-gray-700 bg-gray-50"
                    style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}
                >
                    <br/>
                    {notice.content}
                </div>
                )}
          </div>
        ))}

        {filteredNotices.length === 0 && (
          <div className="p-8 text-center text-gray-500">
            해당 카테고리의 공지사항이 없습니다.
          </div>
        )}
      </div>
    </div>
  );
};

export default NoticeBoard;
