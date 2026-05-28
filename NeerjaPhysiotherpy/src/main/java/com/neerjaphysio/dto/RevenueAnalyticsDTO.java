package com.neerjaphysio.dto;
import java.util.List;
import java.util.Objects;


public class RevenueAnalyticsDTO {

    private Double dailyRevenue;
    private Double weeklyRevenue;
    private Double monthlyRevenue;
    private Double yearlyRevenue;
    private Long totalPayments;
    private Long totalSessions;
    private Double averagePaymentAmount;
    private List<PaymentModeBreakdown> paymentModeBreakdown;

    
    public RevenueAnalyticsDTO() {
				
	}
    
    

    public RevenueAnalyticsDTO(Double dailyRevenue, Double weeklyRevenue, Double monthlyRevenue, Double yearlyRevenue,
			Long totalPayments, Long totalSessions, Double averagePaymentAmount,
			List<PaymentModeBreakdown> paymentModeBreakdown) {
		super();
		this.dailyRevenue = dailyRevenue;
		this.weeklyRevenue = weeklyRevenue;
		this.monthlyRevenue = monthlyRevenue;
		this.yearlyRevenue = yearlyRevenue;
		this.totalPayments = totalPayments;
		this.totalSessions = totalSessions;
		this.averagePaymentAmount = averagePaymentAmount;
		this.paymentModeBreakdown = paymentModeBreakdown;
	}



	@Override
	public int hashCode() {
		return Objects.hash(averagePaymentAmount, dailyRevenue, monthlyRevenue, paymentModeBreakdown, totalPayments,
				totalSessions, weeklyRevenue, yearlyRevenue);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RevenueAnalyticsDTO other = (RevenueAnalyticsDTO) obj;
		return Objects.equals(averagePaymentAmount, other.averagePaymentAmount)
				&& Objects.equals(dailyRevenue, other.dailyRevenue)
				&& Objects.equals(monthlyRevenue, other.monthlyRevenue)
				&& Objects.equals(paymentModeBreakdown, other.paymentModeBreakdown)
				&& Objects.equals(totalPayments, other.totalPayments)
				&& Objects.equals(totalSessions, other.totalSessions)
				&& Objects.equals(weeklyRevenue, other.weeklyRevenue)
				&& Objects.equals(yearlyRevenue, other.yearlyRevenue);
	}

	@Override
	public String toString() {
		return "RevenueAnalyticsDTO [dailyRevenue=" + dailyRevenue + ", weeklyRevenue=" + weeklyRevenue
				+ ", monthlyRevenue=" + monthlyRevenue + ", yearlyRevenue=" + yearlyRevenue
				+ ", totalPayments=" + totalPayments + ", totalSessions=" + totalSessions
				+ ", averagePaymentAmount=" + averagePaymentAmount + "]";
	}

	// Builder Method
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final RevenueAnalyticsDTO dto;

        public Builder() {
            dto = new RevenueAnalyticsDTO();
        }

        public Builder dailyRevenue(Double dailyRevenue) {
            dto.dailyRevenue = dailyRevenue;
            return this;
        }

        public Builder weeklyRevenue(Double weeklyRevenue) {
            dto.weeklyRevenue = weeklyRevenue;
            return this;
        }

        public Builder monthlyRevenue(Double monthlyRevenue) {
            dto.monthlyRevenue = monthlyRevenue;
            return this;
        }

        public Builder yearlyRevenue(Double yearlyRevenue) {
            dto.yearlyRevenue = yearlyRevenue;
            return this;
        }

        public Builder totalPayments(Long totalPayments) {
            dto.totalPayments = totalPayments;
            return this;
        }

        public Builder totalSessions(Long totalSessions) {
            dto.totalSessions = totalSessions;
            return this;
        }

        public Builder averagePaymentAmount(Double averagePaymentAmount) {
            dto.averagePaymentAmount = averagePaymentAmount;
            return this;
        }

        public Builder paymentModeBreakdown(List<PaymentModeBreakdown> paymentModeBreakdown) {
            dto.paymentModeBreakdown = paymentModeBreakdown;
            return this;
        }

        public RevenueAnalyticsDTO build() {
            return dto;
        }
    }

    // Getters and Setters

    public Double getDailyRevenue() {
        return dailyRevenue;
    }

    public void setDailyRevenue(Double dailyRevenue) {
        this.dailyRevenue = dailyRevenue;
    }

    public Double getWeeklyRevenue() {
        return weeklyRevenue;
    }

    public void setWeeklyRevenue(Double weeklyRevenue) {
        this.weeklyRevenue = weeklyRevenue;
    }

    public Double getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(Double monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public Double getYearlyRevenue() {
        return yearlyRevenue;
    }

    public void setYearlyRevenue(Double yearlyRevenue) {
        this.yearlyRevenue = yearlyRevenue;
    }

    public Long getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(Long totalPayments) {
        this.totalPayments = totalPayments;
    }

    public Long getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Long totalSessions) {
        this.totalSessions = totalSessions;
    }

    public Double getAveragePaymentAmount() {
        return averagePaymentAmount;
    }

    public void setAveragePaymentAmount(Double averagePaymentAmount) {
        this.averagePaymentAmount = averagePaymentAmount;
    }

    public List<PaymentModeBreakdown> getPaymentModeBreakdown() {
        return paymentModeBreakdown;
    }

    public void setPaymentModeBreakdown(List<PaymentModeBreakdown> paymentModeBreakdown) {
        this.paymentModeBreakdown = paymentModeBreakdown;
    }

    // Inner Class
    public static class PaymentModeBreakdown {

        private String paymentMode;
        private Long count;
        private Double totalAmount;
        private Double percentage;

        public PaymentModeBreakdown() {
        }

        public PaymentModeBreakdown(String paymentMode,
                                    Long count,
                                    Double totalAmount,
                                    Double percentage) {
            this.paymentMode = paymentMode;
            this.count = count;
            this.totalAmount = totalAmount;
            this.percentage = percentage;
        }
        
        @Override
		public int hashCode() {
			return Objects.hash(count, paymentMode, percentage, totalAmount);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PaymentModeBreakdown other = (PaymentModeBreakdown) obj;
			return Objects.equals(count, other.count) && Objects.equals(paymentMode, other.paymentMode)
					&& Objects.equals(percentage, other.percentage) && Objects.equals(totalAmount, other.totalAmount);
		}

		// Builder
        public static PaymentModeBreakdownBuilder builder() {
            return new PaymentModeBreakdownBuilder();
        }

        public static class PaymentModeBreakdownBuilder {

            private final PaymentModeBreakdown breakdown;

            public PaymentModeBreakdownBuilder() {
                breakdown = new PaymentModeBreakdown();
            }

            public PaymentModeBreakdownBuilder paymentMode(String paymentMode) {
                breakdown.paymentMode = paymentMode;
                return this;
            }

            public PaymentModeBreakdownBuilder count(Long count) {
                breakdown.count = count;
                return this;
            }

            public PaymentModeBreakdownBuilder totalAmount(Double totalAmount) {
                breakdown.totalAmount = totalAmount;
                return this;
            }

            public PaymentModeBreakdownBuilder percentage(Double percentage) {
                breakdown.percentage = percentage;
                return this;
            }

            public PaymentModeBreakdown build() {
                return breakdown;
            }
        }

        // Getters and Setters

        public String getPaymentMode() {
            return paymentMode;
        }

        public void setPaymentMode(String paymentMode) {
            this.paymentMode = paymentMode;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        public Double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }
    }
}