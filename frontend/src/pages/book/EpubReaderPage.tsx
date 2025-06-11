import React, { useEffect, useState, useRef } from "react"
import { Book } from "epubjs"
import { useParams, useNavigate } from "react-router-dom"
import {
  ChevronLeft,
  ChevronRight,
  Menu,
  Moon,
  Settings,
  Sun,
  Minus,
  Plus,
  BookOpen,
  Bookmark,
  BookmarkPlus,
} from "lucide-react"
import { getUserSubscription } from "@/utils/api/userService"
import { toast } from "sonner"

// 타입 정의
interface BookApiResponse {
  code: number
  message: string
  data: {
    epubUri: string
  }
}

interface EpubPage {
  id: string
  chapterId: string
  content: string
  pageNumber: number
  title?: string
}

interface Chapter {
  id: string
  title: string
  href: string
  level: number
  pages: EpubPage[]
}

interface BookmarkItem {
  id: string
  pageIndex: number
  pageId: string
  title: string
  createdAt: number
}

// 유틸리티 함수
const cn = (...classes: (string | boolean | undefined)[]) => {
  return classes.filter(Boolean).join(" ")
}

// UI 컴포넌트 정의
// Button 컴포넌트
const Button = React.forwardRef<
  HTMLButtonElement,
  React.ButtonHTMLAttributes<HTMLButtonElement> & {
    variant?: "default" | "outline" | "ghost"
    size?: "default" | "sm" | "lg" | "icon"
  }
>(({ className, variant = "default", size = "default", ...props }, ref) => {
  const variantStyles = {
    default: "bg-primary text-primary-foreground hover:bg-primary/90",
    outline: "border border-input bg-background hover:bg-accent hover:text-accent-foreground",
    ghost: "hover:bg-accent hover:text-accent-foreground",
  }

  const sizeStyles = {
    default: "h-10 px-4 py-2",
    sm: "h-9 px-3",
    lg: "h-11 px-8",
    icon: "h-10 w-10",
  }

  return (
    <button
      ref={ref}
      className={cn(
        "inline-flex items-center justify-center rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:opacity-50 disabled:pointer-events-none",
        variantStyles[variant],
        sizeStyles[size],
        className,
      )}
      {...props}
    />
  )
})
Button.displayName = "Button"

// Slider 컴포넌트
const Slider = React.forwardRef<
  HTMLDivElement,
  React.HTMLAttributes<HTMLDivElement> & {
    value: number[]
    min: number
    max: number
    step?: number
    onValueChange?: (values: number[]) => void
  }
>(({ className, value, min, max, step = 1, onValueChange, ...props }, ref) => {
  const trackRef = useRef<HTMLDivElement>(null)

  const handlePointerDown = (e: React.PointerEvent<HTMLDivElement>) => {
    const track = trackRef.current
    if (!track) return

    const { left, width } = track.getBoundingClientRect()
    const clientX = e.clientX
    const position = (clientX - left) / width
    const newValue = min + position * (max - min)
    const clampedValue = Math.max(min, Math.min(max, Math.round(newValue / step) * step))

    if (onValueChange) {
      onValueChange([clampedValue])
    }

    const handlePointerMove = (e: PointerEvent) => {
      const { left, width } = track.getBoundingClientRect()
      const clientX = e.clientX
      const position = (clientX - left) / width
      const newValue = min + position * (max - min)
      const clampedValue = Math.max(min, Math.min(max, Math.round(newValue / step) * step))

      if (onValueChange) {
        onValueChange([clampedValue])
      }
    }

    const handlePointerUp = () => {
      document.removeEventListener("pointermove", handlePointerMove)
      document.removeEventListener("pointerup", handlePointerUp)
    }

    document.addEventListener("pointermove", handlePointerMove)
    document.addEventListener("pointerup", handlePointerUp)
  }

  const position = ((value[0] - min) / (max - min)) * 100

  return (
    <div ref={ref} className={cn("relative flex w-full touch-none select-none items-center", className)} {...props}>
      <div
        ref={trackRef}
        className="relative h-2 w-full grow overflow-hidden rounded-full bg-secondary"
        onPointerDown={handlePointerDown}
      >
        <div className="absolute h-full bg-primary" style={{ width: `${position}%` }} />
      </div>
      <div
        className="absolute h-5 w-5 rounded-full border-2 border-primary bg-background"
        style={{ left: `calc(${position}% - 10px)` }}
      />
    </div>
  )
})
Slider.displayName = "Slider"

// Sheet 관련 컴포넌트
const SheetContext = React.createContext<{
  open: boolean
  setOpen: React.Dispatch<React.SetStateAction<boolean>>
}>({
  open: false,
  setOpen: () => {},
})

const Sheet = ({ children }: { children: React.ReactNode }) => {
  const [open, setOpen] = useState(false)
  return <SheetContext.Provider value={{ open, setOpen }}>{children}</SheetContext.Provider>
}

const SheetTrigger = ({ asChild, children }: { asChild?: boolean; children: React.ReactNode }) => {
  const { setOpen } = React.useContext(SheetContext)

  // 클릭 영역을 확장하기 위해 div로 감싸고 패딩 추가
  return (
    <div
      className="p-2 cursor-pointer"
      onClick={(e) => {
        e.stopPropagation()
        setOpen(true)
      }}
    >
      {children}
    </div>
  )
}

const SheetContent = ({
  children,
  side = "right",
  className,
  isDark = false,
}: {
  children: React.ReactNode
  side?: "left" | "right" | "top" | "bottom"
  className?: string
  isDark?: boolean
}) => {
  const { open, setOpen } = React.useContext(SheetContext)
  const ref = useRef<HTMLDivElement>(null)

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (ref.current && !ref.current.contains(event.target as Node)) {
        setOpen(false)
      }
    }

    if (open) {
      document.addEventListener("mousedown", handleClickOutside)
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside)
    }
  }, [open, setOpen])

  if (!open) return null

  const sideStyles = {
    left: "inset-y-0 left-0 h-full w-3/4 max-w-sm border-r",
    right: "inset-y-0 right-0 h-full w-3/4 max-w-sm border-l",
    top: "inset-x-0 top-0 h-96 border-b",
    bottom: "inset-x-0 bottom-0 h-96 border-t",
  }

  return (
    <div className="fixed inset-0 z-50 bg-black/50">
      <div
        ref={ref}
        className={cn(
          "fixed bg-white text-black p-6 shadow-lg transition ease-in-out duration-300",
          sideStyles[side],
          className,
        )}
      >
        {children}
      </div>
    </div>
  )
}

const SheetHeader = ({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) => (
  <div className={cn("flex flex-col space-y-2 text-center sm:text-left", className)} {...props} />
)

const SheetTitle = ({ className, ...props }: React.HTMLAttributes<HTMLHeadingElement>) => (
  <h3 className={cn("text-lg font-semibold", className)} {...props} />
)

const SheetClose = ({ asChild, children }: { asChild?: boolean; children: React.ReactNode }) => {
  const { setOpen } = React.useContext(SheetContext)

  const childrenWithProps = React.Children.map(children, (child) => {
    if (React.isValidElement(child)) {
      return React.cloneElement(child, {
        onClick: (e: React.MouseEvent) => {
          e.stopPropagation()
          setOpen(false)
          if (child.props.onClick) {
            child.props.onClick(e)
          }
        },
      })
    }
    return child
  })

  return <>{childrenWithProps}</>
}

// Tabs 관련 컴포넌트
const TabsContext = React.createContext<{
  value: string
  setValue: React.Dispatch<React.SetStateAction<string>>
}>({
  value: "",
  setValue: () => {},
})

const Tabs = ({
  defaultValue,
  className,
  children,
}: {
  defaultValue: string
  className?: string
  children: React.ReactNode
}) => {
  const [value, setValue] = useState(defaultValue)

  return (
    <TabsContext.Provider value={{ value, setValue }}>
      <div className={cn("", className)}>{children}</div>
    </TabsContext.Provider>
  )
}

const TabsList = ({ className, children }: { className?: string; children: React.ReactNode }) => (
  <div className={cn("flex", className)}>{children}</div>
)

const TabsTrigger = ({
  value,
  className,
  children,
}: { value: string; className?: string; children: React.ReactNode }) => {
  const { value: selectedValue, setValue } = React.useContext(TabsContext)

  return (
    <button
      className={cn(
        "inline-flex items-center justify-center whitespace-nowrap rounded-sm px-3 py-1.5 text-sm font-medium ring-offset-background transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50",
        selectedValue === value
          ? "bg-background text-foreground shadow-sm"
          : "text-muted-foreground hover:bg-muted hover:text-foreground",
        className,
      )}
      onClick={() => setValue(value)}
    >
      {children}
    </button>
  )
}

const TabsContent = ({
  value,
  className,
  children,
}: { value: string; className?: string; children: React.ReactNode }) => {
  const { value: selectedValue } = React.useContext(TabsContext)

  if (selectedValue !== value) return null

  return <div className={cn("mt-2", className)}>{children}</div>
}

// Select 관련 컴포넌트
const SelectContext = React.createContext<{
  value: string
  onValueChange: (value: string) => void
  open: boolean
  setOpen: React.Dispatch<React.SetStateAction<boolean>>
}>({
  value: "",
  onValueChange: () => {},
  open: false,
  setOpen: () => {},
})

const Select = ({
  value,
  onValueChange,
  children,
}: {
  value: string
  onValueChange: (value: string) => void
  children: React.ReactNode
}) => {
  const [open, setOpen] = useState(false)

  return (
    <SelectContext.Provider value={{ value, onValueChange, open, setOpen }}>
      <div className="relative">{children}</div>
    </SelectContext.Provider>
  )
}

const SelectTrigger = ({ children, className }: { children?: React.ReactNode; className?: string }) => {
  const { value, setOpen } = React.useContext(SelectContext)

  return (
    <button
      className={cn(
        "flex h-10 w-full items-center justify-between rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50",
        className,
      )}
      onClick={(e) => {
        e.stopPropagation()
        setOpen((prev) => !prev)
      }}
    >
      {children || value}
    </button>
  )
}

const SelectValue = () => {
  const { value } = React.useContext(SelectContext)
  return <span>{value}</span>
}

const SelectContent = ({ children, className }: { children: React.ReactNode; className?: string }) => {
  const { open, setOpen } = React.useContext(SelectContext)
  const ref = useRef<HTMLDivElement>(null)

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (ref.current && !ref.current.contains(event.target as Node)) {
        setOpen(false)
      }
    }

    if (open) {
      document.addEventListener("mousedown", handleClickOutside)
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside)
    }
  }, [open, setOpen])

  if (!open) return null

  return (
    <div
      ref={ref}
      className={cn(
        "absolute z-50 min-w-[8rem] overflow-hidden rounded-md border bg-white text-black shadow-md animate-in fade-in-80",
        "w-full mt-1",
        className,
      )}
    >
      <div className="p-1">{children}</div>
    </div>
  )
}

const SelectItem = ({
  value,
  children,
  className,
}: { value: string; children: React.ReactNode; className?: string }) => {
  const { value: selectedValue, onValueChange, setOpen } = React.useContext(SelectContext)

  const handleClick = (e: React.MouseEvent) => {
    e.stopPropagation()
    onValueChange(value)
    setOpen(false)
  }

  return (
    <div
      className={cn(
        "relative flex w-full cursor-default select-none items-center rounded-sm py-1.5 pl-8 pr-2 text-sm outline-none focus:bg-accent focus:text-accent-foreground data-[disabled]:pointer-events-none data-[disabled]:opacity-50",
        selectedValue === value && "bg-accent text-accent-foreground",
        className,
      )}
      onClick={handleClick}
    >
      {selectedValue === value && (
        <span className="absolute left-2 flex h-3.5 w-3.5 items-center justify-center">
          <svg width="15" height="15" viewBox="0 0 15 15" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path
              d="M11.4669 3.72684C11.7558 3.91574 11.8369 4.30308 11.648 4.59198L7.39799 11.092C7.29783 11.2452 7.13556 11.3467 6.95402 11.3699C6.77247 11.3931 6.58989 11.3355 6.45446 11.2124L3.70446 8.71241C3.44905 8.48022 3.43023 8.08494 3.66242 7.82953C3.89461 7.57412 4.28989 7.55529 4.5453 7.78749L6.75292 9.79441L10.6018 3.90792C10.7907 3.61902 11.178 3.53795 11.4669 3.72684Z"
              fill="currentColor"
              fillRule="evenodd"
              clipRule="evenodd"
            ></path>
          </svg>
        </span>
      )}
      <span className="ml-2">{children}</span>
    </div>
  )
}

// 아이콘 버튼 래퍼 컴포넌트 - 클릭 영역 확장을 위한 컴포넌트
const IconButton = ({
  icon,
  onClick,
  className,
  disabled = false,
}: {
  icon: React.ReactNode
  onClick: (e: React.MouseEvent) => void
  className?: string
  disabled?: boolean
}) => {
  return (
    <div
      className={cn("p-2 cursor-pointer", disabled && "opacity-50 cursor-not-allowed", className)}
      onClick={(e) => {
        if (!disabled) {
          e.stopPropagation()
          onClick(e)
        }
      }}
    >
      {icon}
    </div>
  )
}

const EpubReaderPage: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [pages, setPages] = useState<EpubPage[]>([])
  const [chapters, setChapters] = useState<Chapter[]>([])
  const [loading, setLoading] = useState(true)
  const [theme, setTheme] = useState<"light" | "dark">("light")
  const [fontSize, setFontSize] = useState<number>(18)
  const [lineHeight, setLineHeight] = useState<number>(1.6)
  const [fontFamily, setFontFamily] = useState<string>("serif")
  const [bookTitle, setBookTitle] = useState<string>("")
  const [currentPage, setCurrentPage] = useState<number>(0)
  const [showControls, setShowControls] = useState<boolean>(true)
  const [isTransitioning, setIsTransitioning] = useState<boolean>(false)
  const [bookmarks, setBookmarks] = useState<BookmarkItem[]>([])
  const pagesContainerRef = useRef<HTMLDivElement>(null)

  // 구독 상태 확인 및 리다이렉트
  useEffect(() => {
    const checkSubscription = async () => {
      try {
        const subscriptionData = await getUserSubscription()

        // 구독이 없거나 활성화되지 않은 경우 마이페이지로 리다이렉트
        if (!subscriptionData || !subscriptionData.stillValid) {
          toast.error("구독권이 필요합니다", {
            description: "이 콘텐츠를 이용하려면 구독이 필요합니다.",
            duration: 5000,
          })

          // 마이페이지로 리다이렉트
          navigate("/my-page")
        }
      } catch (error) {
        console.error("구독 정보 확인 실패:", error)
        toast.error("구독 정보 확인 실패", {
          description: "구독 정보를 확인하는 중 오류가 발생했습니다.",
        })

        // 오류 발생 시에도 마이페이지로 리다이렉트
        navigate("/my-page")
      }
    }

    checkSubscription()
  }, [id, navigate])

  // 북마크 로드
  useEffect(() => {
    if (id) {
      const savedBookmarks = localStorage.getItem(`book-${id}-bookmarks`)
      if (savedBookmarks) {
        try {
          setBookmarks(JSON.parse(savedBookmarks))
        } catch (e) {
          console.error("북마크 로드 실패:", e)
        }
      }
    }
  }, [id])

  // 북마크 저장
  const saveBookmarks = (newBookmarks: BookmarkItem[]) => {
    if (id) {
      localStorage.setItem(`book-${id}-bookmarks`, JSON.stringify(newBookmarks))
      setBookmarks(newBookmarks)
    }
  }

  // 북마크 추가
  const addBookmark = () => {
    if (!id || pages.length === 0) return

    const currentPageData = pages[currentPage]
    const newBookmark: BookmarkItem = {
      id: `bookmark-${Date.now()}`,
      pageIndex: currentPage,
      pageId: currentPageData.id,
      title: currentPageData.title || bookTitle,
      createdAt: Date.now(),
    }

    // 이미 같은 페이지에 북마크가 있는지 확인
    const existingBookmarkIndex = bookmarks.findIndex((b) => b.pageIndex === currentPage)

    if (existingBookmarkIndex !== -1) {
      // 이미 북마크가 있으면 제거
      const newBookmarks = [...bookmarks]
      newBookmarks.splice(existingBookmarkIndex, 1)
      saveBookmarks(newBookmarks)
    } else {
      // 북마크 추가
      saveBookmarks([...bookmarks, newBookmark])
    }
  }

  // 북마크 제거
  const removeBookmark = (bookmarkId: string) => {
    const newBookmarks = bookmarks.filter((b) => b.id !== bookmarkId)
    saveBookmarks(newBookmarks)
  }

  // 북마크 여부 확인
  const isBookmarked = (pageIndex: number) => {
    return bookmarks.some((b) => b.pageIndex === pageIndex)
  }

  // 책 데이터 로드
  useEffect(() => {
    const loadBook = async () => {
      if (!id) return

      setLoading(true)
      try {
        // 실제 앱에서는 실제 API 엔드포인트로 대체
        const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "/api"
        const res = await fetch(`${apiBaseUrl}/viewer/books/${id}`)
        const data: BookApiResponse = await res.json()
        const epub = new Book(data.data.epubUri)
        await epub.ready

        setBookTitle((await epub.loaded.metadata).title || "제목 없음")

        const toc = await epub.loaded.navigation
        const chaptersData = toc.toc

        const chaptersList: Chapter[] = []
        const pagesList: EpubPage[] = []
        let pageCounter = 1

        for (let i = 0; i < chaptersData.length; i++) {
          const item = chaptersData[i]
          const href = item.href
          const chapter = await epub.load(href)
          const html =
            typeof chapter === "string" ? chapter : chapter.body?.innerHTML || chapter.documentElement?.outerHTML || ""

          const chapterPages: EpubPage[] = []

          // 페이지 분할 개선
          const tempDiv = document.createElement("div")
          tempDiv.innerHTML = html

          // 콘텐츠 정리
          const scripts = tempDiv.querySelectorAll("script")
          scripts.forEach((script) => script.remove())

          const paragraphs = tempDiv.querySelectorAll("p, div, h1, h2, h3, h4, h5, h6")

          let current = ""
          let count = 0
          const wordsPerPage = 300 // 원하는 페이지 길이에 따라 조정

          paragraphs.forEach((p, idx) => {
            current += p.outerHTML
            count += p.textContent?.split(/\s+/).length || 0

            if (count > wordsPerPage || idx === paragraphs.length - 1) {
              const page = {
                id: `ch${i + 1}-pg${pageCounter}`,
                chapterId: `ch${i + 1}`,
                content: current,
                pageNumber: pageCounter++,
                title: item.label,
              }

              chapterPages.push(page)
              pagesList.push(page)

              current = ""
              count = 0
            }
          })

          chaptersList.push({
            id: `ch${i + 1}`,
            title: item.label,
            href: item.href,
            level: item.level || 0,
            pages: chapterPages,
          })
        }

        setPages(pagesList)
        setChapters(chaptersList)
      } catch (err) {
        console.error("EPUB 로드 실패:", err)
      } finally {
        setLoading(false)
      }
    }

    loadBook()
  }, [id])

  // 페이지 이동 처리
  const goToPage = (pageIndex: number) => {
    if (pageIndex < 0 || pageIndex >= pages.length) return

    setIsTransitioning(true)
    setCurrentPage(pageIndex)

    // 페이지로 스크롤
    if (pagesContainerRef.current) {
      const pageElement = pagesContainerRef.current.children[pageIndex] as HTMLElement
      if (pageElement) {
        pagesContainerRef.current.scrollTo({
          left: pageElement.offsetLeft,
          behavior: "smooth",
        })
      }
    }

    // 애니메이션 완료 후 전환 상태 재설정
    setTimeout(() => {
      setIsTransitioning(false)
    }, 500)
  }

  const nextPage = () => goToPage(currentPage + 1)
  const prevPage = () => goToPage(currentPage - 1)

  const goToChapter = (chapterId: string) => {
    const firstPageInChapter = pages.findIndex((page) => page.chapterId === chapterId)
    if (firstPageInChapter !== -1) {
      goToPage(firstPageInChapter)
    }
  }

  // 테마 전환 함수
  const toggleTheme = () => {
    setTheme((prev) => (prev === "light" ? "dark" : "light"))
  }

  // 키보드 네비게이션 처리
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "ArrowRight") {
        nextPage()
      } else if (e.key === "ArrowLeft") {
        prevPage()
      }
    }

    window.addEventListener("keydown", handleKeyDown)
    return () => window.removeEventListener("keydown", handleKeyDown)
  }, [currentPage, pages.length])

  // 스크롤 동기화 처리
  useEffect(() => {
    const handleScroll = () => {
      if (!pagesContainerRef.current || isTransitioning) return

      const container = pagesContainerRef.current
      const scrollLeft = container.scrollLeft
      const containerWidth = container.clientWidth

      // 가장 보이는 페이지 찾기
      const pageIndex = Math.round(scrollLeft / containerWidth)
      if (pageIndex !== currentPage) {
        setCurrentPage(pageIndex)
      }
    }

    const container = pagesContainerRef.current
    if (container) {
      container.addEventListener("scroll", handleScroll)
      return () => container.removeEventListener("scroll", handleScroll)
    }
  }, [currentPage, isTransitioning])

  // 비활성 시 컨트롤 자동 숨김
  useEffect(() => {
    let timeout: NodeJS.Timeout

    const resetTimer = () => {
      clearTimeout(timeout)
      setShowControls(true)

      timeout = setTimeout(() => {
        setShowControls(false)
      }, 3000)
    }

    const handleActivity = () => {
      resetTimer()
    }

    window.addEventListener("mousemove", handleActivity)
    window.addEventListener("click", handleActivity)
    window.addEventListener("keydown", handleActivity)

    resetTimer()

    return () => {
      clearTimeout(timeout)
      window.removeEventListener("mousemove", handleActivity)
      window.removeEventListener("click", handleActivity)
      window.removeEventListener("keydown", handleActivity)
    }
  }, [])

  // 읽기 위치 저장 및 복원
  useEffect(() => {
    if (id && currentPage > 0 && !loading) {
      localStorage.setItem(`book-${id}-position`, currentPage.toString())
    }
  }, [currentPage, id, loading])

  useEffect(() => {
    if (id && pages.length > 0 && !loading) {
      const savedPosition = localStorage.getItem(`book-${id}-position`)
      if (savedPosition) {
        const position = Number.parseInt(savedPosition, 10)
        if (position >= 0 && position < pages.length) {
          goToPage(position)
        }
      }
    }
  }, [id, pages.length, loading])

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="flex flex-col items-center gap-4">
          <BookOpen className="w-12 h-12 animate-pulse" />
          <p className="text-lg">책을 불러오는 중입니다...</p>
        </div>
      </div>
    )
  }

  // CSS 스타일 정의
  const containerStyle = cn(
    "h-screen w-full flex flex-col relative overflow-hidden",
    theme === "dark" ? "bg-[#121212] text-white" : "bg-[#f8f6f0] text-[#333333]",
  )

  const topBarStyle = cn(
    "flex justify-between items-center px-6 py-3 transition-opacity duration-300",
    theme === "dark" ? "bg-[#1e1e1e]" : "bg-[#f2f2f7]",
    showControls ? "opacity-100" : "opacity-0 pointer-events-none",
  )

  const pageStyle = cn(
    "snap-center shrink-0 w-full h-full overflow-y-auto px-8 py-12",
    theme === "dark" ? "bg-[#121212]" : "bg-[#f8f6f0]",
  )

  const contentStyle = cn("max-w-[700px] mx-auto", theme === "dark" ? "text-white" : "text-[#333333]")

  const navButtonStyle = cn(
    "absolute inset-y-0 flex items-center transition-opacity duration-300",
    showControls ? "opacity-100" : "opacity-0 pointer-events-none",
  )

  const bottomBarStyle = cn(
    "py-3 px-6 flex items-center justify-between transition-opacity duration-300",
    theme === "dark" ? "bg-[#1e1e1e]" : "bg-[#f2f2f7]",
    showControls ? "opacity-100" : "opacity-0 pointer-events-none",
  )

  // 목차 선택 시 배경색 스타일
  const activeChapterStyle = "bg-[oklch(48.8%_0.243_264.376)] text-white hover:bg-[oklch(48.8%_0.243_264.376)]"

  return (
    <div className={containerStyle}>
      {/* 상단바 - 컨트롤이 표시될 때만 보임 */}
      <div className={topBarStyle}>
        <div className="flex items-center gap-2">
          <Sheet>
            <SheetTrigger>
              <Menu size={20} className={theme === "dark" ? "text-white" : "text-[#333333]"} />
            </SheetTrigger>
            <SheetContent side="left">
              <SheetHeader>
                <SheetTitle className="text-black text-left">{bookTitle}</SheetTitle>
              </SheetHeader>
              <Tabs defaultValue="chapters" className="mt-6">
                <TabsList className="grid w-full grid-cols-2">
                  <TabsTrigger value="chapters">목차</TabsTrigger>
                  <TabsTrigger value="bookmarks">북마크</TabsTrigger>
                </TabsList>
                <TabsContent value="chapters" className="mt-4 max-h-[70vh] overflow-y-auto">
                  <div className="space-y-2">
                    {chapters.map((chapter) => {
                      // 현재 챕터인지 확인
                      const isCurrentChapter =
                        currentPage >= pages.findIndex((p) => p.chapterId === chapter.id) &&
                        currentPage <= pages.findIndex((p) => p.chapterId === chapter.id) + chapter.pages.length - 1

                      return (
                        <div key={chapter.id} className="py-1">
                          <SheetClose asChild>
                            <Button
                              variant="ghost"
                              className={cn(
                                "w-full justify-start text-left pl-[calc(0.5rem*chapter.level)] text-black",
                                isCurrentChapter ? activeChapterStyle : "hover:bg-gray-100",
                              )}
                              onClick={() => goToChapter(chapter.id)}
                            >
                              {chapter.title}
                            </Button>
                          </SheetClose>
                        </div>
                      )
                    })}
                  </div>
                </TabsContent>
                <TabsContent value="bookmarks">
                  {bookmarks.length > 0 ? (
                    <div className="space-y-2">
                      {bookmarks.map((bookmark) => {
                        // 현재 북마크인지 확인
                        const isCurrentBookmark = currentPage === bookmark.pageIndex

                        return (
                          <div key={bookmark.id} className="flex items-center justify-between py-1">
                            <SheetClose asChild>
                              <Button
                                variant="ghost"
                                className={cn(
                                  "w-full justify-start text-left",
                                  isCurrentBookmark ? activeChapterStyle : "text-black hover:bg-gray-100",
                                )}
                                onClick={() => goToPage(bookmark.pageIndex)}
                              >
                                <div className="flex items-center gap-2">
                                  <Bookmark size={16} />
                                  <span>{bookmark.title}</span>
                                </div>
                              </Button>
                            </SheetClose>
                            <Button
                              variant="ghost"
                              size="sm"
                              className="h-8 w-8 p-0 text-black hover:bg-gray-100"
                              onClick={(e) => {
                                e.stopPropagation()
                                removeBookmark(bookmark.id)
                              }}
                            >
                              &times;
                            </Button>
                          </div>
                        )
                      })}
                    </div>
                  ) : (
                    <div className="py-4 text-center text-gray-500">북마크가 없습니다</div>
                  )}
                </TabsContent>
              </Tabs>
            </SheetContent>
          </Sheet>
          <span className="font-medium text-sm">{pages[currentPage]?.title || bookTitle}</span>
        </div>

        <div className="flex items-center gap-2">
          {/* 북마크 버튼 추가 - 클릭 영역 확장 */}
          <IconButton
            icon={
              isBookmarked(currentPage) ? <Bookmark size={20} className="fill-current" /> : <BookmarkPlus size={20} />
            }
            onClick={addBookmark}
          />

          <Sheet>
            <SheetTrigger>
              <Settings size={20} className={theme === "dark" ? "text-white" : "text-[#333333]"} />
            </SheetTrigger>
            <SheetContent>
              <SheetHeader>
                <SheetTitle className="text-black">읽기 설정</SheetTitle>
              </SheetHeader>
              <div className="py-6 space-y-6">
                <div className="space-y-2">
                  <div className="flex justify-between items-center">
                    <span className="text-black">글꼴 크기</span>
                    <div className="flex items-center gap-2">
                      <Button
                        variant="outline"
                        size="icon"
                        className="h-8 w-8 rounded-full text-black border-gray-300"
                        onClick={() => setFontSize(Math.max(12, fontSize - 1))}
                      >
                        <Minus size={16} />
                      </Button>
                      <span className="w-8 text-center text-black">{fontSize}</span>
                      <Button
                        variant="outline"
                        size="icon"
                        className="h-8 w-8 rounded-full text-black border-gray-300"
                        onClick={() => setFontSize(Math.min(32, fontSize + 1))}
                      >
                        <Plus size={16} />
                      </Button>
                    </div>
                  </div>
                </div>

                <div className="space-y-2">
                  <span className="text-black">글꼴</span>
                  <Select value={fontFamily} onValueChange={setFontFamily}>
                    <SelectTrigger className="border-gray-300 text-black bg-white">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="serif">Serif</SelectItem>
                      <SelectItem value="sans-serif">Sans-serif</SelectItem>
                      <SelectItem value="monospace">Monospace</SelectItem>
                      <SelectItem value="'Apple SD Gothic Neo', sans-serif">Apple SD Gothic Neo</SelectItem>
                      <SelectItem value="'Noto Sans KR', sans-serif">Noto Sans KR</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <div className="flex justify-between">
                    <span className="text-black">줄 간격</span>
                    <span className="text-black">{lineHeight.toFixed(1)}</span>
                  </div>
                  <Slider
                    value={[lineHeight]}
                    min={1.0}
                    max={2.5}
                    step={0.1}
                    onValueChange={(values) => setLineHeight(values[0])}
                  />
                </div>

                <div className="space-y-2">
                  <span className="text-black">테마</span>
                  <div className="grid grid-cols-2 gap-2">
                    <Button
                      variant={theme === "light" ? "default" : "outline"}
                      className="justify-start gap-2 text-black border-gray-300"
                      onClick={() => setTheme("light")}
                    >
                      <Sun size={16} />
                      <span>라이트</span>
                    </Button>
                    <Button
                      variant={theme === "dark" ? "default" : "outline"}
                      className="justify-start gap-2 text-black border-gray-300"
                      onClick={() => setTheme("dark")}
                    >
                      <Moon size={16} />
                      <span>다크</span>
                    </Button>
                  </div>
                </div>
              </div>
            </SheetContent>
          </Sheet>

          {/* 테마 전환 버튼 - 클릭 영역 확장 */}
          <IconButton
            icon={
              theme === "light" ? (
                <Moon size={20} className="text-[#333333]" />
              ) : (
                <Sun size={20} className="text-white" />
              )
            }
            onClick={toggleTheme}
          />
        </div>
      </div>

      {/* 책 내용 */}
      <div
        ref={pagesContainerRef}
        className="flex-1 overflow-x-auto overflow-y-hidden snap-x snap-mandatory flex scroll-smooth"
        style={{
          scrollbarWidth: "none",
          msOverflowStyle: "none",
        }}
      >
        {pages.map((page) => (
          <div
            key={page.id}
            className={pageStyle}
            style={{
              scrollbarWidth: "none",
              msOverflowStyle: "none",
            }}
          >
            <div
              className={contentStyle}
              style={{
                fontSize: `${fontSize}px`,
                fontFamily,
                lineHeight: lineHeight,
              }}
              dangerouslySetInnerHTML={{ __html: page.content }}
            />
          </div>
        ))}
      </div>

      {/* 네비게이션 버튼 */}
      <div className={`${navButtonStyle} left-0`}>
        <IconButton
          icon={<ChevronLeft size={24} className={theme === "dark" ? "text-white" : "text-[#333333]"} />}
          onClick={prevPage}
          disabled={currentPage <= 0}
          className="h-12 w-12 rounded-full bg-opacity-50 backdrop-blur-sm"
        />
      </div>

      <div className={`${navButtonStyle} right-0`}>
        <IconButton
          icon={<ChevronRight size={24} className={theme === "dark" ? "text-white" : "text-[#333333]"} />}
          onClick={nextPage}
          disabled={currentPage >= pages.length - 1}
          className="h-12 w-12 rounded-full bg-opacity-50 backdrop-blur-sm"
        />
      </div>

      {/* 하단 진행 표시줄 */}
      <div className={bottomBarStyle}>
        <span className="text-sm">
          {currentPage + 1} / {pages.length}
        </span>

        <div className="w-full max-w-md mx-4">
          <Slider
            value={[currentPage]}
            min={0}
            max={pages.length - 1}
            step={1}
            onValueChange={(values) => goToPage(values[0])}
          />
        </div>

        <span className="text-sm">{Math.round(((currentPage + 1) / pages.length) * 100)}%</span>
      </div>

      {/* 스타일 정의 */}
      <style jsx>{`
        /* 스크롤바 숨기기 (Chrome, Safari, Opera) */
        ::-webkit-scrollbar {
          display: none;
        }

        /* EPUB 콘텐츠 스타일링 */
        .epub-content {
          max-width: 100%;
        }

        .epub-content img {
          margin: 1rem auto;
          max-width: 100%;
          height: auto;
        }

        .epub-content h1,
        .epub-content h2,
        .epub-content h3,
        .epub-content h4,
        .epub-content h5,
        .epub-content h6 {
          font-weight: bold;
          margin-bottom: 1rem;
          margin-top: 1.5rem;
        }

        .epub-content p {
          margin: 0.75rem 0;
        }

        .epub-content a {
          text-decoration: underline;
        }
      `}</style>
    </div>
  )
}

export default EpubReaderPage