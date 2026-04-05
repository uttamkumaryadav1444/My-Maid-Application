package MaidRepository.maid.dto;

public class SalaryCalculationDTO {
    private Long maidId;
    private String maidName;
    private Double monthlySalary;
    private Integer workingDays = 26;
    private Integer leaves;
    private Double overtimeAmount;
    private Double perDaySalary;
    private Double leaveDeduction;
    private Double finalSalary;

    // Constructors
    public SalaryCalculationDTO() {}

    // Getters and Setters
    public Long getMaidId() { return maidId; }
    public void setMaidId(Long maidId) { this.maidId = maidId; }
    public String getMaidName() { return maidName; }
    public void setMaidName(String maidName) { this.maidName = maidName; }
    public Double getMonthlySalary() { return monthlySalary; }
    public void setMonthlySalary(Double monthlySalary) { this.monthlySalary = monthlySalary; }
    public Integer getWorkingDays() { return workingDays; }
    public void setWorkingDays(Integer workingDays) { this.workingDays = workingDays; }
    public Integer getLeaves() { return leaves; }
    public void setLeaves(Integer leaves) { this.leaves = leaves; }
    public Double getOvertimeAmount() { return overtimeAmount; }
    public void setOvertimeAmount(Double overtimeAmount) { this.overtimeAmount = overtimeAmount; }
    public Double getPerDaySalary() { return perDaySalary; }
    public void setPerDaySalary(Double perDaySalary) { this.perDaySalary = perDaySalary; }
    public Double getLeaveDeduction() { return leaveDeduction; }
    public void setLeaveDeduction(Double leaveDeduction) { this.leaveDeduction = leaveDeduction; }
    public Double getFinalSalary() { return finalSalary; }
    public void setFinalSalary(Double finalSalary) { this.finalSalary = finalSalary; }
}