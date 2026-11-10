package com.epms.payroll.service;
import com.epms.payroll.dto.*; import com.epms.payroll.entity.Employee; import org.springframework.stereotype.Service; import java.math.*;
@Service public class PayrollCalculator {
 private static final RoundingMode RM=RoundingMode.HALF_UP;
 private BigDecimal m(BigDecimal a,BigDecimal b){return a.multiply(b).setScale(2,RM);} private BigDecimal p(BigDecimal x){return x==null?BigDecimal.ZERO:x;}
 public PayrollResult calculate(Employee e,PayrollRequest r){
   BigDecimal rate=p(e.getHourlyRate()); BigDecimal regH=p(r.regularHours()), dtH=p(r.doubletimeHours()), ex1=p(r.extraHours1()), ex2=p(r.extraHours2());
   BigDecimal dtRate=rate.multiply(BigDecimal.valueOf(2)); BigDecimal reg=m(rate,regH), dt=m(dtRate,dtH), ep1=m(p(r.extraRate1()),ex1), ep2=m(p(r.extraRate2()),ex2);
   BigDecimal gross=reg.add(dt).add(ep1).add(ep2).setScale(2,RM);
   BigDecimal health=p(e.getHealthDeduction()); BigDecimal k401=m(gross,p(e.getTraditional401kPct()).divide(BigDecimal.valueOf(100),8,RM));
   BigDecimal roth=m(gross,p(e.getRothPct()).divide(BigDecimal.valueOf(100),8,RM)); BigDecimal kcu=m(gross,p(e.getTraditionalCatchUpPct()).divide(BigDecimal.valueOf(100),8,RM)); BigDecimal rcu=m(gross,p(e.getRothCatchUpPct()).divide(BigDecimal.valueOf(100),8,RM));
   BigDecimal federalBase=gross.subtract(health).subtract(k401).subtract(kcu).setScale(2,RM);
   BigDecimal ficaBase=gross.subtract(health).setScale(2,RM);
   BigDecimal fed=m(federalBase,p(r.federalRate()).divide(BigDecimal.valueOf(100),8,RM)); BigDecimal ss=m(ficaBase,p(r.socialSecurityRate()).divide(BigDecimal.valueOf(100),8,RM));
   BigDecimal med=m(ficaBase,p(r.medicareRate()).divide(BigDecimal.valueOf(100),8,RM)); BigDecimal pa=m(ficaBase,p(r.paRate()).divide(BigDecimal.valueOf(100),8,RM)); BigDecimal local=m(ficaBase,p(r.localRate()).divide(BigDecimal.valueOf(100),8,RM));
   BigDecimal lst=p(r.lst()).setScale(2,RM); BigDecimal sui=m(ficaBase,p(r.paSuiRate()).divide(BigDecimal.valueOf(100),8,RM)); BigDecimal match=m(k401,p(r.employerMatchRate()).divide(BigDecimal.valueOf(100),8,RM));
   BigDecimal net=gross.subtract(fed).subtract(ss).subtract(med).subtract(pa).subtract(local).subtract(lst).subtract(sui).subtract(health).subtract(k401).subtract(roth).subtract(kcu).subtract(rcu).setScale(2,RM);
   return new PayrollResult(gross,federalBase,fed,ss,med,pa,local,lst,sui,health,k401,roth,kcu,rcu,match,net);
 }
}
