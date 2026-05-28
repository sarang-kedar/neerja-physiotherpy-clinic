package com.neerjaphysio.dto;

import java.util.Objects;

public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Long timestamp;
    //add getter setter all args constructor and no args constructor tostring and equals and hashcode
    public ApiResponse() {
	}
    public ApiResponse(boolean success, String message, T data, Long timestamp) {
    			this.success = success;
    			this.message = message;
    			this.data = data;
    			this.timestamp = timestamp;
    			
    }
     
    // Builder Method
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {

        private final ApiResponse<T> response;

        public Builder() {
            response = new ApiResponse<>();
        }

        public Builder<T> success(boolean success) {
            response.success = success;
            return this;
        }

        public Builder<T> message(String message) {
            response.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            response.data = data;
            return this;
        }

        public Builder<T> timestamp(Long timestamp) {
            response.timestamp = timestamp;
            return this;
        }

        public ApiResponse<T> build() {
            return response;
        }
    }

    // Success Response
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    // Error Response
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    // Failure Response (alias for error)
    public static <T> ApiResponse<T> failure(String message) {
        return error(message);
    }

    // Getters and Setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
	@Override
	public int hashCode() {
		return Objects.hash(data, message, success, timestamp);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApiResponse<?> other = (ApiResponse<?>) obj;
		return Objects.equals(data, other.data) && Objects.equals(message, other.message) && success == other.success
				&& Objects.equals(timestamp, other.timestamp);
	}
	@Override
	public String toString() {
		return "ApiResponse [success=" + success + ", message=" + message + ", data=" + data + ", timestamp="
				+ timestamp + "]";
	}

}