export interface AuthContextType {
  isAuthenticated: boolean;
  email: string | null;
  login: (token: string, refreshToken: string, email: string) => void;
  logout: () => Promise<void>;
  isLoading: boolean;
}
