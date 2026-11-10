package com.epms.payroll.service;
import com.epms.payroll.dto.*; import com.epms.payroll.entity.*; import com.epms.payroll.exception.BusinessRuleException; import com.epms.payroll.repository.*; import org.springframework.stereotype.Service; import java.math.BigDecimal; import java.math.RoundingMode; import java.time.LocalDate; import java.util.List;
@Service public class PayrollService {private final EmployeeRepository er;private final PayrollRepository pr;private final PayrollCalculator calc;private final TaxAccountingService taxService;private final PayrollAccountingService accountingService;private final AuditLogService auditService;
 public PayrollService(EmployeeRepository e,PayrollRepository p,PayrollCalculator c,TaxAccountingService t,PayrollAccountingService a,AuditLogService au){er=e;pr=p;calc=c;taxService=t;accountingService=a;auditService=au;}

 public PayrollResult calculate(PayrollRequest r){Employee e=er.findById(r.employeeId()).orElseThrow();PayrollResult raw=calc.calculate(e,r);return applySocialSecurityCap(r,raw);}

 private PayrollResult applySocialSecurityCap(PayrollRequest r,PayrollResult x){
  int year=r.payDate()!=null?r.payDate().getYear():LocalDate.now().getYear();
  BigDecimal ficaBase=x.grossPay().subtract(x.health());
  BigDecimal cappedWages=taxService.capSocialSecurityWages(r.employeeId(),year,ficaBase);
  BigDecimal ssRate=r.socialSecurityRate()==null?BigDecimal.ZERO:r.socialSecurityRate();
  BigDecimal cappedSsTax=cappedWages.multiply(ssRate).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
  if(cappedSsTax.compareTo(x.socialSecurityTax())>=0) return x;
  BigDecimal diff=x.socialSecurityTax().subtract(cappedSsTax);
  BigDecimal newNet=x.netPay().add(diff).setScale(2,RoundingMode.HALF_UP);
  return new PayrollResult(x.grossPay(),x.federalTaxableWages(),x.federalTax(),cappedSsTax,x.medicareTax(),x.paTax(),x.localTax(),x.lst(),x.paSui(),x.health(),x.traditional401k(),x.roth(),x.traditionalCatchUp(),x.rothCatchUp(),x.employerMatch(),newNet);
 }

 public Payroll save(PayrollRequest r,String performedBy){Employee e=er.findById(r.employeeId()).orElseThrow();PayrollResult x=calculate(r);Payroll p=new Payroll();p.setEmployee(e);p.setPeriodBeginning(r.periodBeginning());p.setPeriodEnding(r.periodEnding());p.setPayDate(r.payDate());p.setRegularHours(r.regularHours());p.setDoubletimeHours(r.doubletimeHours());p.setExtraHours1(r.extraHours1());p.setExtraHours2(r.extraHours2());p.setRegularRate(e.getHourlyRate());p.setDoubletimeRate(e.getHourlyRate().multiply(BigDecimal.valueOf(2)));p.setExtraRate1(r.extraRate1());p.setExtraRate2(r.extraRate2());p.setGrossPay(x.grossPay());p.setFederalTaxableWages(x.federalTaxableWages());p.setFederalTax(x.federalTax());p.setSocialSecurityTax(x.socialSecurityTax());p.setMedicareTax(x.medicareTax());p.setPaTax(x.paTax());p.setLocalTax(x.localTax());p.setLst(x.lst());p.setPaSui(x.paSui());p.setHealth(x.health());p.setTraditional401k(x.traditional401k());p.setRoth(x.roth());p.setTraditionalCatchUp(x.traditionalCatchUp());p.setRothCatchUp(x.rothCatchUp());p.setEmployerMatch(x.employerMatch());p.setNetPay(x.netPay());Payroll saved=pr.save(p);accountingService.postForPayroll(saved);auditService.record("PAYROLL",saved.getId(),"CREATE",null,null,"gross="+saved.getGrossPay()+" net="+saved.getNetPay()+" status="+saved.getStatus(),performedBy,null);return saved;}

 public List<Payroll> byEmployee(Long id){return pr.findByEmployeeId(id);}

 public List<Payroll> all(){return pr.findAll();}

 public Payroll updateStatus(Long id,String status,String performedBy){Payroll p=pr.findById(id).orElseThrow();String old=p.getStatus();p.setStatus(status);Payroll saved=pr.save(p);auditService.record("PAYROLL",id,"STATUS_CHANGE","status",old,status,performedBy,null);return saved;}

 public Payroll voidPayroll(Long id,String reason,String performedBy){Payroll p=pr.findById(id).orElseThrow();if("PROCESSED".equals(p.getStatus())||"REVERSED".equals(p.getStatus()))throw new BusinessRuleException("A processed or already-reversed payroll cannot be voided — use Reverse instead.");String old=p.getStatus();p.setStatus("VOID");Payroll saved=pr.save(p);auditService.record("PAYROLL",id,"VOID","status",old,"VOID",performedBy,reason);return saved;}

 public Payroll reversePayroll(Long id,String reason,String performedBy){Payroll p=pr.findById(id).orElseThrow();if(!"PROCESSED".equals(p.getStatus()))throw new BusinessRuleException("Only a PROCESSED payroll can be reversed.");String old=p.getStatus();p.setStatus("REVERSED");Payroll saved=pr.save(p);accountingService.reverseForPayroll(saved);auditService.record("PAYROLL",id,"REVERSE","status",old,"REVERSED",performedBy,reason);return saved;}
}
