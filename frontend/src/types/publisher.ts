export interface Publisher {
  id: number;
  name: string;
}

export interface PublisherResponse {
  code: number;
  message: string;
  data: {
    publishers: Publisher[];
  };
}
