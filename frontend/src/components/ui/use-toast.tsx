// src/components/ui/use-toast.tsx
import React, { createContext, useContext, useState } from "react";
import { X } from "lucide-react";
import { Button } from "@/components/ui/button";

// 토스트 타입 정의
type ToastVariant = "default" | "destructive";

interface Toast {
  id: string;
  title: string;
  description?: string;
  variant?: ToastVariant;
}

interface ToastContextType {
  toasts: Toast[];
  toast: (props: Omit<Toast, "id">) => void;
  dismiss: (id: string) => void;
}

// 토스트 컨텍스트 생성
const ToastContext = createContext<ToastContextType | undefined>(undefined);

export const ToastProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [toasts, setToasts] = useState<Toast[]>([]);

  // 토스트 추가 함수
  const toast = ({
    title,
    description,
    variant = "default",
  }: Omit<Toast, "id">) => {
    const id = Math.random().toString(36).substr(2, 9);
    const newToast = { id, title, description, variant };

    setToasts((prevToasts) => [...prevToasts, newToast]);

    // 3초 후 자동으로 토스트 제거
    setTimeout(() => {
      dismiss(id);
    }, 3000);
  };

  // 토스트 제거 함수
  const dismiss = (id: string) => {
    setToasts((prevToasts) => prevToasts.filter((toast) => toast.id !== id));
  };

  return (
    <ToastContext.Provider value={{ toasts, toast, dismiss }}>
      {children}
      {/* 토스트 컨테이너 */}
      <div className="fixed bottom-0 right-0 p-4 z-50 flex flex-col gap-2 max-w-md w-full pointer-events-none">
        {toasts.map((toast) => (
          <div
            key={toast.id}
            className={`bg-white border shadow-lg rounded-lg p-4 transition-all duration-300 transform translate-y-0 opacity-100 pointer-events-auto ${
              toast.variant === "destructive"
                ? "border-red-500"
                : "border-gray-200"
            }`}
          >
            <div className="flex items-start justify-between">
              <div>
                <h3
                  className={`font-medium ${
                    toast.variant === "destructive"
                      ? "text-red-500"
                      : "text-gray-900"
                  }`}
                >
                  {toast.title}
                </h3>
                {toast.description && (
                  <p className="text-sm text-gray-500 mt-1">
                    {toast.description}
                  </p>
                )}
              </div>
              <Button
                variant="ghost"
                size="icon"
                className="h-6 w-6 rounded-full"
                onClick={() => dismiss(toast.id)}
              >
                <X className="h-4 w-4" />
              </Button>
            </div>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};

export const useToast = () => {
  const context = useContext(ToastContext);
  if (context === undefined) {
    throw new Error("useToast must be used within a ToastProvider");
  }
  return context;
};
