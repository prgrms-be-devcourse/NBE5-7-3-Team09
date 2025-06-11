import React from "react";
import { cn } from "@/lib/utils";
import { Github, Code, Users, BookOpen, Globe, Headphones, Phone, Mail } from "lucide-react";

interface FooterProps extends React.HTMLAttributes<HTMLElement> {}

const Footer = ({ className, ...props }: FooterProps) => {
  return (
    <footer
      className={cn("w-full bg-stone-50 border-t mt-5", className)}
      {...props}
    >
      <div className="container px-4 md:px-6 py-6 mx-auto ">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
          <div className="flex flex-col">
            <h3 className="text-sm font-medium flex items-center gap-2">
              <Code className="h-4 w-4" />
              <span>프로젝트</span>
            </h3>
            <div className="mt-2 text-sm text-muted-foreground">
              <p>프로그래머스 데브코스 팀 프로젝트</p>
              <p>백엔드 5기 | 7회차 | 2차 프로젝트</p>
            </div>
          </div>

          <div className="flex flex-col">
            <h3 className="text-sm font-medium flex items-center gap-2">
              <Users className="h-4 w-4" />
              <span>팀</span>
            </h3>
            <div className="mt-2 text-sm text-muted-foreground">
              <p>팀 9 (NBE5-7-2-Team09)</p>
            </div>
          </div>

          <div className="flex flex-col">
            <a
              href="/cs/notice"
              className="text-sm font-medium flex items-center gap-2 text-muted-foreground hover:text-primary transition-colors"
            >
              <Headphones className="h-4 w-4" />
              <span>공지사항</span>
            </a>

            <a
              href="/cs/faq"
              className="mt-2 flex items-center gap-1 text-muted-foreground hover:text-primary transition-colors text-sm"
            >
              <Phone className="h-3 w-3" />
              <span>자주 묻는 질문</span>
            </a>
          </div>


          <div className="flex flex-col">
            <h3 className="text-sm font-medium flex items-center gap-2">
              <BookOpen className="h-4 w-4" />
              <span>리소스</span>
            </h3>
            <div className="mt-2 flex flex-col gap-2 text-sm">
              <a
                href="https://github.com/prgrms-be-devcourse/NBE5-7-2-Team09"
                target="_blank"
                rel="noopener noreferrer"
                className="text-muted-foreground hover:text-primary transition-colors flex items-center gap-1"
              >
                <Github className="h-4 w-4" />
                <span>GitHub 레포지토리</span>
              </a>
              <a
                href="https://programmers.co.kr"
                target="_blank"
                rel="noopener noreferrer"
                className="text-muted-foreground hover:text-primary transition-colors flex items-center gap-1"
              >
                <Globe className="h-4 w-4" />
                <span>프로그래머스</span>
              </a>
            </div>
          </div>
        </div>

        <div className="pt-4 border-t flex flex-col md:flex-row justify-between items-center">
          <p className="text-sm text-muted-foreground">
            © {new Date().getFullYear()} NBE5-7-2-Team09. All rights reserved.
          </p>
          <p className="text-xs text-muted-foreground mt-2 md:mt-0">
            Made with 💻 by Team 9 at Programmers DevCourse
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;