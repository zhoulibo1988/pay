package com.pay.manger.controller.payv2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.core.teamwork.base.util.ObjectUtil;
import com.core.teamwork.base.util.date.DateStyle;
import com.core.teamwork.base.util.date.DateUtil;
import com.core.teamwork.base.util.http.HttpUtil;
import com.core.teamwork.base.util.page.PageObject;
import com.core.teamwork.base.util.redis.RedisDBUtil;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2BussCompanyApp;
import com.pay.business.merchant.entity.Payv2BussSupportPayWay;
import com.pay.business.merchant.entity.Payv2Channel;
import com.pay.business.merchant.service.Payv2BussCompanyAppService;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.merchant.service.Payv2BussSupportPayWayService;
import com.pay.business.merchant.service.Payv2ChannelService;
import com.pay.business.order.entity.Payv2PayOrder;
import com.pay.business.order.entity.Payv2PayOrderBeanVO;
import com.pay.business.order.entity.Payv2PayOrderNotify;
import com.pay.business.order.mapper.Payv2PayOrderMapper;
import com.pay.business.order.service.Payv2PayOrderGroupService;
import com.pay.business.order.service.Payv2PayOrderNotifyService;
import com.pay.business.order.service.Payv2PayOrderRefundService;
import com.pay.business.order.service.Payv2PayOrderService;
import com.pay.business.payway.entity.Payv2PayWay;
import com.pay.business.payway.entity.Payv2PayWayRate;
import com.pay.business.payway.service.Payv2PayWayRateService;
import com.pay.business.payway.service.Payv2PayWayService;
import com.pay.business.util.CSVUtils;
import com.pay.business.util.DecimalUtil;
import com.pay.business.util.ParameterEunm;
import com.pay.business.util.PayFinalUtil;
import com.pay.business.util.PaySignUtil;
import com.pay.manger.controller.admin.BaseManagerController;
import com.pay.manger.interceptor.DateMoneyVo;

/**
 * @ClassName: Payv2PayOrderController
 * @Description:订单管理
 * @author zhoulibo
 * @date 2016年12月7日 下午3:10:42
 */
@Controller
@RequestMapping("/Payv2PayOrder/*")
public class Payv2PayOrderController extends BaseManagerController<Payv2PayOrder, Payv2PayOrderMapper> {

	private static final Logger logger = Logger.getLogger(Payv2PayOrderController.class);
	@Autowired
	private Payv2PayOrderService payv2PayOrderService;
	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;
	@Autowired
	private Payv2BussCompanyAppService payv2BussCompanyAppService;
	@Autowired
	private Payv2PayWayService payv2PayWayService;
	@Autowired
	private Payv2PayOrderRefundService payv2PayOrderRefundService;
	@Autowired
	private Payv2PayOrderGroupService payv2PayOrderGroupService;
	@Autowired
	private Payv2PayWayRateService payv2PayWayRateService;
	@Autowired
	private Payv2BussSupportPayWayService Payv2BussSupportPayWayService;
	@Autowired
	private Payv2ChannelService payv2ChannelService;
	@Autowired
	private Payv2PayOrderNotifyService payv2PayOrderNotifyService;
	
	/**
	 * @Title: getPayv2PayOrderList
	 * @Description:获取订单管理列表
	 * @param map
	 * @return 设定文件
	 * @return ModelAndView 返回类型
	 * @date 2016年12月7日 下午3:12:21
	 * @throws
	 */
	@RequestMapping("getPayv2PayOrderList")
	public ModelAndView getPayv2PayOrderList(@RequestParam Map<String, Object> map) {
		ModelAndView av = new ModelAndView("payv2/order/payv2PayOrder-list");
//		PageObject<Payv2PayOrder> pageList = payv2PayOrderService.getPageObject(map, 0);
		//List<Payv2PayOrder> orderList = pageList.getDataList();
		//获取各个项目总数
//		Map<String,Object> sumMap=payv2PayOrderService.selectOrderSum(map);
		// 获取商户 app 支付渠道
		Payv2BussCompany company = new Payv2BussCompany();
//		company.setIsDelete(2);
		List<Payv2BussCompany> companyList = payv2BussCompanyService.selectByObject(company);
		Payv2BussCompanyApp app = new Payv2BussCompanyApp();
//		app.setIsDelete(2);
		List<Payv2BussCompanyApp> appList = payv2BussCompanyAppService.selectByObject(app);
		Payv2PayWay pay = new Payv2PayWay();
//		pay.setIsDelete(2);
		List<Payv2PayWay> payList = payv2PayWayService.selectByObject(pay);
		
		Map<String, Object> param = new HashMap<>();
		param.put("payWayId", map.get("payWayId"));
//		param.put("status", 1);
//		param.put("isDelete", 2);
		List<Payv2PayWayRate> rateList = payv2PayWayRateService.query(param);
		// 获取渠道
		param.clear();
		List<Payv2Channel> channelList = payv2ChannelService.query(param);
		
		av.addObject("rateList", rateList);
		av.addObject("channelList", channelList);
		av.addObject("companyList", companyList);
		av.addObject("appList", appList);
		av.addObject("payList", payList);
		av.addObject("map", map);
//		av.addObject("list", pageList);
//		av.addObject("sumMap",sumMap);
		return av;
	}
	
	@RequestMapping("searchPayv2PayOrderList")
	public ModelAndView searchPayv2PayOrderList(@RequestParam Map<String, Object> map) {
		ModelAndView av = new ModelAndView("payv2/order/payv2PayOrder-list");
		if(null != map.get("orderNum") && !"".equals(map.get("orderNum").toString())){
			String orderNum[] = map.get("orderNum").toString().split(",");
			map.put("orderNumArray", orderNum);
		}
		Date nowDate = new Date();
		if(null == map.get("createTimeBegin") || "".equals(map.get("createTimeBegin").toString())){
			map.put("createTimeBegin",DateUtil.DateToString(DateUtil.addDay(nowDate, -30), DateStyle.YYYY_MM_DD_HH_MM_SS) );
		}
		if(null == map.get("createTimeEnd") || "".equals(map.get("createTimeEnd").toString())){
			map.put("createTimeEnd",DateUtil.DateToString(nowDate,DateStyle.YYYY_MM_DD_HH_MM_SS));
		}
		PageObject<Payv2PayOrder> pageList = payv2PayOrderService.getPageObject(map, 0);
		//List<Payv2PayOrder> orderList = pageList.getDataList();
		long time = System.currentTimeMillis();
		//获取各个项目总数
		Map<String,Object> sumMap=payv2PayOrderService.selectOrderSum(map);
		System.out.println("统计sql耗时:"+(System.currentTimeMillis()-time));
		// 获取商户 app 支付渠道
		Payv2BussCompany company = new Payv2BussCompany();
//		company.setIsDelete(2);
		List<Payv2BussCompany> companyList = payv2BussCompanyService.selectByObject(company);
		Payv2BussCompanyApp app = new Payv2BussCompanyApp();
//		app.setIsDelete(2);
		List<Payv2BussCompanyApp> appList = payv2BussCompanyAppService.selectByObject(app);
		Payv2PayWay pay = new Payv2PayWay();
//		pay.setIsDelete(2);
		List<Payv2PayWay> payList = payv2PayWayService.selectByObject(pay);
		
		Map<String, Object> param = new HashMap<>();
//		param.put("status", 1);
//		param.put("isDelete", 2);
		param.put("payWayId", map.get("payWayId"));
		List<Payv2PayWayRate> rateList = payv2PayWayRateService.query(param);
		// 获取渠道
		param.clear();
		List<Payv2Channel> channelList = payv2ChannelService.query(param);
		
		av.addObject("rateList", rateList);
		av.addObject("channelList", channelList);
		av.addObject("companyList", companyList);
		av.addObject("appList", appList);
		av.addObject("payList", payList);
		av.addObject("map", map);
		av.addObject("list", pageList);
		av.addObject("sumMap",sumMap);
		return av;
	}
	
	@ResponseBody
	@RequestMapping("searchPayv3PayOrderList")
	public Map<String, Object> searchPayv3PayOrderList(@RequestParam Map<String, Object> map) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if(null != map.get("orderNum").toString() && !"".equals(map.get("orderNum").toString())){
			String orderNum[] = map.get("orderNum").toString().split(",");
			map.put("orderNumArray", orderNum);
		}
		Date nowDate = new Date();
		if(null == map.get("createTimeBegin") || "".equals(map.get("createTimeBegin").toString())){
			map.put("createTimeBegin",DateUtil.DateToString(DateUtil.addDay(nowDate, -30), DateStyle.YYYY_MM_DD_HH_MM) );
		}
		if(null == map.get("createTimeEnd") || "".equals(map.get("createTimeEnd").toString())){
			map.put("createTimeEnd",DateUtil.DateToString(nowDate,DateStyle.YYYY_MM_DD_HH_MM));
		}
		PageObject<Payv2PayOrder> pageList = payv2PayOrderService.getPageObject(map, 0);
		//List<Payv2PayOrder> orderList = pageList.getDataList();
		//获取各个项目总数		
		Map<String,Object> sumMap=payv2PayOrderService.selectOrderSum(map);
		// 获取商户 app 支付渠道
		Payv2BussCompany company = new Payv2BussCompany();
		company.setIsDelete(2);
		List<Payv2BussCompany> companyList = payv2BussCompanyService.selectByObject(company);
		Payv2BussCompanyApp app = new Payv2BussCompanyApp();
		app.setIsDelete(2);
		List<Payv2BussCompanyApp> appList = payv2BussCompanyAppService.selectByObject(app);
		Payv2PayWay pay = new Payv2PayWay();
		pay.setIsDelete(2);
		List<Payv2PayWay> payList = payv2PayWayService.selectByObject(pay);
		
		Map<String, Object> param = new HashMap<>();
		param.put("status", 1);
		param.put("isDelete", 2);
		param.put("payWayId", map.get("payWayId"));
		List<Payv2PayWayRate> rateList = payv2PayWayRateService.query(param);
		
		returnMap.put("rateList", rateList);
		returnMap.put("companyList", companyList);
		returnMap.put("appList", appList);
		returnMap.put("payList", payList);
		returnMap.put("map", map);
		returnMap.put("list", pageList);
		returnMap.put("sumMap",sumMap);
		return returnMap;
	}

	/**
	 * @throws Exception
	 * @Title: callbackOrder
	 * @Description:回调支付接口
	 * @param map
	 * @return 设定文件
	 * @return Map<String,Object> 返回类型
	 * @date 2016年12月21日 上午11:48:38
	 * @throws
	 */
	@ResponseBody
	@RequestMapping("callbackOrder")
	public Map<String, Object> callbackOrder(@RequestParam Map<String, Object> map) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		Map<String, Object> param = new HashMap<>();
		Payv2PayOrder payv2PayOrder = new Payv2PayOrder();
		payv2PayOrder.setId(Long.valueOf(map.get("id").toString()));
		payv2PayOrder = payv2PayOrderService.selectSingle(payv2PayOrder);
		if (payv2PayOrder != null && payv2PayOrder.getOrderType() == 1) {
			param.put("result_code", 200); // 成功
			param.put("pay_order_num", payv2PayOrder.getOrderNum()); // 订单
			param.put("buss_order_num", payv2PayOrder.getMerchantOrderNum()); // 商户订单
			param.put("pay_money", payv2PayOrder.getPayMoney()); // 支付金额
			param.put("pay_discount_money", payv2PayOrder.getPayMoney()); // 最终支付金额
			param.put("pay_time", DateStr(payv2PayOrder.getPayTime())); // 支付时间
																		// yyyyMMddHHmmss
			// 查询商家app 获取回调地址
			Payv2BussCompanyApp pbca = new Payv2BussCompanyApp();
			pbca.setId(payv2PayOrder.getAppId());
			pbca = payv2BussCompanyAppService.selectSingle(pbca);
			// 参数签名
			String sign = PaySignUtil.getSign(param, pbca.getAppSecret());
			param.put("sign", sign);
			try {
				// 通知商户
				Integer code = HttpUtil.getCode(payv2PayOrder.getNotifyUrl() == null ? pbca.getCallbackUrl() : payv2PayOrder.getNotifyUrl(), param);
				// 通知商户请求失败
				if (code != 200) {
					payv2PayOrder.setPayStatus(PayFinalUtil.PAY_ORDER_SUCCESS_BACKFAIL); // 支付成功回调失败
					payv2PayOrderService.update(payv2PayOrder);
				} else {
					payv2PayOrder.setPayStatus(PayFinalUtil.PAY_ORDER_SUCCESS); // 支付成功
					payv2PayOrderService.update(payv2PayOrder);
				}

			} catch (Exception e) {
				payv2PayOrder.setPayStatus(PayFinalUtil.PAY_ORDER_SUCCESS_BACKFAIL); // 支付成功回调失败
				payv2PayOrderService.update(payv2PayOrder);
				logger.info("回调商户失败");
				e.printStackTrace();
			}
		}
		returnMap.put("resultCode", 200);
		return returnMap;
	}

	private String DateStr(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = sdf.format(date);
		return str;
	}
	
	/**
	 * @Title: getOrderDetail
	 * @Description:获取订单详情
	 * @param map
	 * @return 设定文件
	 * @return ModelAndView 返回类型
	 * @date 2017年7月3日 上午9:27:21
	 * @throws
	 */
	@RequestMapping("getOrderDetail")
	public ModelAndView getOrderDetail(@RequestParam Map<String, Object> map) {
		ModelAndView mav = new ModelAndView("payv2/order/payv2PayOrder_detail_new");
		Payv2PayOrder payv2PayOrder = new Payv2PayOrder();
		payv2PayOrder.setId(Long.valueOf(map.get("orderId").toString()));
		payv2PayOrder = payv2PayOrderService.selectSingleDetail(payv2PayOrder);
		mav.addObject("payv2PayOrder", payv2PayOrder);
		return mav;
	}
	
	
	/**
	* @Title: exportExcelOrder 
	* @Description:订单导出
	* @param map
	* @param workbook
	* @param request
	* @param response
	* @return    设定文件 
	* @return Map<String,Object>    返回类型 
	* @date 2017年2月10日 下午5:29:47 
	* @throws
	*/
	@ResponseBody
	@RequestMapping("/exportExcelOrder")
	public Map<String, Object> exportExcelOrder(@RequestParam Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		// 搜索
		map.put("curPage", 1);
		map.put("pageData", 300000);
		if(null != map.get("orderNum").toString() && !"".equals(map.get("orderNum").toString())){
			String orderNum[] = map.get("orderNum").toString().split(",");
			map.put("orderNumArray", orderNum);
		}
		Date nowDate = new Date();
		if(null == map.get("createTimeBegin") || "".equals(map.get("createTimeBegin").toString())){
			map.put("createTimeBegin",DateUtil.DateToString(DateUtil.addDay(nowDate, -30), DateStyle.YYYY_MM_DD_HH_MM) );
		}
		if(null == map.get("createTimeEnd") || "".equals(map.get("createTimeEnd").toString())){
			map.put("createTimeEnd",DateUtil.DateToString(nowDate,DateStyle.YYYY_MM_DD_HH_MM));
		}
		PageObject<Payv2PayOrder> pageList = payv2PayOrderService.getPageObject(map, 0);
		List<Payv2PayOrder> orderList = pageList.getDataList();
		OutputStream out = null;
		InputStream in = null;
		try {
			if (null != orderList && orderList.size() > 0) {
				// 导出
				CSVUtils<Object> csv = new CSVUtils<Object>();
				//TestExportExcel<Payv2PayOrderBeanVO> ex = new TestExportExcel<Payv2PayOrderBeanVO>();
				//String[] headers = { "支付集订单号", "商户订单号", "来源商户", "来源应用", "支付渠道", "订单金额(元)", "实际支付金额(元)", "优惠金额(元)", "退款金额(元)", "订单状态", "创建时间", "订单支付时间", "订单回调时间" };
				//Object[] headers = { "平台订单号", "商户订单号", "来源商户", "来源应用", "支付渠道","订单名词","订单金额(元)","服务费", "订单状态", "交易时间"};
				Object[] headers = { "平台订单号", "商户订单号", "来源渠道", "来源商户", "来源应用", "支付平台", "支付通道名称", "支付通道编号", "商品名称","订单金额(元)","商户手续费","成本手续费", "订单状态","备注", "交易时间"};
				//List<Payv2PayOrderBeanVO> dataset = new ArrayList<Payv2PayOrderBeanVO>();
				List<Object> dataset = new ArrayList<Object>();
				for (Payv2PayOrder payv2PayOrder : orderList) {
					Payv2PayOrderBeanVO bte = new Payv2PayOrderBeanVO();
					// 平台订单号
					bte.setOrderNum(payv2PayOrder.getOrderNum());
					// 商户订单号
					bte.setMerchantOrderNum(payv2PayOrder.getMerchantOrderNum());
					bte.setChannelName(payv2PayOrder.getChannelName());
					// 来源商户
					bte.setCompanyName(payv2PayOrder.getCompanyName());
					// 来源应用
					if(payv2PayOrder.getOrderType() == 1){
						bte.setAppName(payv2PayOrder.getAppName());
					}else if(payv2PayOrder.getOrderType() == 2){
						bte.setAppName("收款码");
					}					
					// 支付平台
					bte.setWayName(payv2PayOrder.getWayName());
					// 支付通道名称
					bte.setRateName(payv2PayOrder.getRateName());
					//支付通道编号
					bte.setPayWayName(payv2PayOrder.getPayWayName());
					//订单名称
					bte.setOrderName(payv2PayOrder.getOrderName());
					// 订单金额(元)
					bte.setPayMoney(payv2PayOrder.getPayMoney());
					//商户手续费
					bte.setPayWayMoney(payv2PayOrder.getPayWayMoney());
					//成本手续费
					bte.setCostRateMoney(payv2PayOrder.getCostRateMoney());
					// 实际支付金额(元)
					//bte.setPayDiscountMoney(payv2PayOrder.getPayDiscountMoney());
					// 优惠金额(元)
					//bte.setDiscountMoney(payv2PayOrder.getDiscountMoney());
					// 退款金额(元)
					//bte.setRefundMoney(payv2PayOrder.getRefundMoney());
					// 订单状态
					if (payv2PayOrder.getPayStatus() == 1) {
						bte.setPayStatusName("支付成功");
					} else if (payv2PayOrder.getPayStatus() == 2) {
						bte.setPayStatusName("支付失败");
					} else if (payv2PayOrder.getPayStatus() == 3) {
						bte.setPayStatusName("未支付");
					} else if (payv2PayOrder.getPayStatus() == 4) {
						bte.setPayStatusName("超时");
					} else if (payv2PayOrder.getPayStatus() == 5) {
						bte.setPayStatusName("扣款成功回调失败");
					}
					//备注
					bte.setRemark(payv2PayOrder.getRemark());
					// 交易时间
					bte.setCreateTime(payv2PayOrder.getCreateTime());
					// 回调时间
					//bte.setCallbackTime(payv2PayOrder.getCallbackTime());

					dataset.add(bte);
				}
				/*OutputStream out = response.getOutputStream();
				String filename = "订单列表" + new Date().getTime() + ".csv";// 设置下载时客户端Excel的名称
				filename = URLEncoder.encode(filename, "UTF-8");// 处理中文文件名
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-disposition", "attachment;filename=" + filename);
				ex.exportExcel(headers, dataset, out);*/
				out = response.getOutputStream();
				String filename = "订单列表" + new Date().getTime() + ".csv";
				filename = URLEncoder.encode(filename, "UTF-8");// 处理中文文件名
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-disposition", "attachment;filename=" + filename);
				List<Object> headList = Arrays.asList(headers);
				File csvFile = csv.commonCSV(headList, dataset, null, filename);
				in = new FileInputStream(csvFile);
				int b;  
	            while((b=in.read())!= -1)  
	            {  
	                out.write(b);  
	            }  
			} else {
				returnMap.put("status", -1);// 失败
			}
		} catch (Exception e) {
			logger.error("导出订单列表.xls错误", e);
			e.printStackTrace();
		}finally{
			try {
				if(in!=null){
					in.close();
				}
				if(out!=null){
					out.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return returnMap;
	}
////////////////////////////////////////////////////app实时流水统计工具//////////////////////////////////////////////////////////////////////////////////
	/**
	 * getDateMoneyByRate app统计工具--》通道金额统计
	 * @return 设定文件 Map<String,Object> 返回类型
	 */
	@ResponseBody
	@RequestMapping("getDateMoneyByRate")
	public Map<String, Object> getDateMoneyByRate() {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			
			Date as = new Date();
			String time = DateUtil.DateToString(as, "yyyyMMdd");
			Map<String, Object> map = new HashMap<String, Object>();
			// 删除是否
			map.put("isDelete", 2);
			// 开启是否
			map.put("status", 1);
			List<Payv2PayWayRate> rateList = payv2PayWayRateService.query(map);
			List<Map<String,Object>> countList = payv2PayOrderService.selectByRate(map);
			DateMoneyVo dateMoneyVo = null;
			int redisType=1;
			List<DateMoneyVo> voList = new ArrayList<DateMoneyVo>();
			for (Payv2PayWayRate payv2PayWayRate : rateList) {
				//String rateRedisKey=rateId+"RID"+time+"TYPE"+redisType;
				String rateRedisKey = payv2PayWayRate.getId()+"RID"+ time+"TYPE"+redisType;
				String rateValue = RedisDBUtil.redisDao.getString(rateRedisKey);
				Map<String,Object> mapCount = getMapByRate(countList, payv2PayWayRate.getId());
				//支付成功率
				double payR= 0;
				if(mapCount!=null){
					double allCount = Double.valueOf(mapCount.get("allCount").toString());
					double payCount = Double.valueOf(mapCount.get("payCount").toString());
					System.out.println(payv2PayWayRate.getRateName()+"总笔数："+allCount+";支付笔数："+payCount);
					if(allCount!=0){
						payR = DecimalUtil.getCeil(payCount*100/allCount,2);
					}
				}else{
					payR=0;
				}
				if (rateValue != null) {
					dateMoneyVo = new DateMoneyVo();
					dateMoneyVo.setId(payv2PayWayRate.getId());
					BigDecimal b = new BigDecimal(rateValue);
					double money = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					dateMoneyVo.setMoney(money);
					dateMoneyVo.setName(payv2PayWayRate.getRateName());
					dateMoneyVo.setPayR(payR);
					voList.add(dateMoneyVo);
				} else {
					dateMoneyVo = new DateMoneyVo();
					dateMoneyVo.setId(payv2PayWayRate.getId());
					dateMoneyVo.setMoney(0.00);
					dateMoneyVo.setName(payv2PayWayRate.getRateName());
					dateMoneyVo.setPayR(payR);
					voList.add(dateMoneyVo);
				}
			}
			// 获取总金额
			Map<String, String> sumMoney = RedisDBUtil.redisDao.getStringMapAll(time);
			double money=0.00;
			if(sumMoney.containsKey("dateMoney")){
				BigDecimal b = new BigDecimal(sumMoney.get("dateMoney"));
				money = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			returnMap.put("sumDateMoney",money);
			returnMap.put("dateTime", DateUtil.DateToString(as, "yyyy-MM-dd HH:mm:ss"));
			if(voList.size()>0){
				ListSort(voList);
			}
			returnMap.put("dateSumList", voList);
			returnMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, returnMap);
		} catch (Exception e) {
			returnMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null);
			e.printStackTrace();
		}
		return returnMap;

	}

	/**
	 * getDateMoneyByCompany APP移动统计工具---》商户数据统计
	 * @return 设定文件 Map<String,Object> 返回类型
	 */
	@ResponseBody
	@RequestMapping("getDateMoneyByCompany")
	public Map<String, Object> getDateMoneyByCompany() {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try{
			Date as = new Date();
			String time = DateUtil.DateToString(as, "yyyyMMdd");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("isDelete", 2);
			map.put("companyStatus", 2);
			List<Payv2BussCompany> companyList = payv2BussCompanyService.query(map);
			List<Map<String,Object>> countList = payv2PayOrderService.selectByCom(map);
			DateMoneyVo dateMoneyVo = null;
			int redisType=1;
			List<DateMoneyVo> voList = new ArrayList<DateMoneyVo>();
			for (Payv2BussCompany payv2BussCompany : companyList) {
				//String companyRedisKey1=companyId+"CID"+time+"TYPE"+redisType;
				String companyRedisKey = payv2BussCompany.getId() +"CID"+ time+"TYPE"+redisType;
				String companyValue = RedisDBUtil.redisDao.getString(companyRedisKey);
				Map<String,Object> mapCount = getMapByCom(countList, payv2BussCompany.getId());
				//支付成功率
				double payR= 0;
				if(mapCount!=null){
					double allCount = Double.valueOf(mapCount.get("allCount").toString());
					double payCount = Double.valueOf(mapCount.get("payCount").toString());
					System.out.println(payv2BussCompany.getCompanyName()+"总笔数："+allCount+";支付笔数："+payCount);
					if(allCount!=0){
						payR = DecimalUtil.getCeil(payCount*100/allCount,2);
					}
				}
				if (companyValue != null) {
					dateMoneyVo = new DateMoneyVo();
					dateMoneyVo.setId(payv2BussCompany.getId());
					dateMoneyVo.setName(payv2BussCompany.getCompanyName());
					BigDecimal b = new BigDecimal(companyValue);
					double money = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					dateMoneyVo.setMoney(money);
					dateMoneyVo.setPayR(payR);
					voList.add(dateMoneyVo);
				} else {
					dateMoneyVo = new DateMoneyVo();
					dateMoneyVo.setId(payv2BussCompany.getId());
					dateMoneyVo.setName(payv2BussCompany.getCompanyName());
					dateMoneyVo.setMoney(0.00);
					dateMoneyVo.setPayR(payR);
					voList.add(dateMoneyVo);
				}
			}
			// 获取总金额
			Map<String, String> sumMoney = RedisDBUtil.redisDao.getStringMapAll(time);
			double money=0.00;
			if(sumMoney.containsKey("dateMoney")){
				BigDecimal b = new BigDecimal(sumMoney.get("dateMoney"));
				money = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			returnMap.put("sumDateMoney",money);
			returnMap.put("dateTime", DateUtil.DateToString(as, "yyyy-MM-dd HH:mm:ss"));
			if(voList.size()>0){
				ListSort(voList);
			}
			returnMap.put("dateSumList", voList);
			returnMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, returnMap);
		} catch (Exception e) {
			returnMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null);
			e.printStackTrace();
		}
		return returnMap;
	}

	/**
	 * getDateMoneyByCompanyToRate 根据商户ID获取旗下的支付通道的数据统计
	 * @param map
	 * @return 设定文件 Map<String,Object> 返回类型
	 */
	@ResponseBody
	@RequestMapping("getDateMoneyByCToR")
	public Map<String, Object> getDateMoneyByCompanyToRate(@RequestParam Map<String, Object> map) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try{
			boolean isNotNull = ObjectUtil.checkObject(new String[] { "companyId" }, map);
			if (isNotNull) {
				Date as = new Date();
				String time = DateUtil.DateToString(as, "yyyyMMdd");
				Long companyId = Long.valueOf(map.get("companyId").toString());
				// 获取商户旗下支持的支付通道ID
	//			map.put("isDelete", 2);
	//			map.put("payWayStatus", 1);
				map.put("parentId",companyId);
				List<Payv2BussSupportPayWay> wayList = Payv2BussSupportPayWayService.query(map);
				List<Map<String,Object>> countList = payv2PayOrderService.selectByRateCom(map);
				DateMoneyVo dateMoneyVo = null;
				List<DateMoneyVo> voList = new ArrayList<DateMoneyVo>();
				double sumDateMoney = 0.00;
				int redisType=1;
				for (Payv2BussSupportPayWay payv2BussSupportPayWay : wayList) {
					if(payv2BussSupportPayWay.getRateId()!=null){
						//String companyIdAndRateIdKey=companyId+"CID"+rateId+"RID"+time+"TYPE"+redisType;
						String companyIdAndRateIdKey = companyId+"CID"+ payv2BussSupportPayWay.getRateId()+"RID"+ time+"TYPE"+redisType;
						String companyIdAndRateValue = RedisDBUtil.redisDao.getString(companyIdAndRateIdKey);
						Map<String,Object> mapCount = getMapByRateCom(countList, companyId,payv2BussSupportPayWay.getRateId());
						//支付成功率
						double payR= 0;
						if(mapCount!=null){
							double allCount = Double.valueOf(mapCount.get("allCount").toString());
							double payCount = Double.valueOf(mapCount.get("payCount").toString());
							if(allCount!=0){
								payR = DecimalUtil.getCeil(payCount*100/allCount,2);
							}
						}
						// 根据rateId获取支付通道名字
						Payv2PayWayRate payv2PayWayRate = payv2PayWayRateService.queryByid(payv2BussSupportPayWay.getRateId());
						if (companyIdAndRateValue != null) {
							dateMoneyVo = new DateMoneyVo();
							dateMoneyVo.setId(payv2BussSupportPayWay.getRateId());
							dateMoneyVo.setName(payv2PayWayRate.getRateName());
							BigDecimal b = new BigDecimal(companyIdAndRateValue);
							double money = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							dateMoneyVo.setMoney(money);
							double companyIdAndRateMoney = Double.valueOf(companyIdAndRateValue);
							dateMoneyVo.setPayR(payR);
							// 某商户的总数据统计
							sumDateMoney = sumDateMoney + companyIdAndRateMoney;
							voList.add(dateMoneyVo);
						} else {
							dateMoneyVo = new DateMoneyVo();
							dateMoneyVo.setId(payv2BussSupportPayWay.getRateId());
							dateMoneyVo.setName(payv2PayWayRate.getRateName());
							dateMoneyVo.setMoney(0.00);
							dateMoneyVo.setPayR(payR);
							voList.add(dateMoneyVo);
						}
					}
				}
				BigDecimal b = new BigDecimal(sumDateMoney);
				sumDateMoney = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				returnMap.put("sumDateMoney", sumDateMoney);
				returnMap.put("dateTime", DateUtil.DateToString(as, "yyyy-MM-dd HH:mm:ss"));
				if(voList.size()>0){
					ListSort(voList);
				}
				returnMap.put("dateSumList", voList);
				returnMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, returnMap);
			} else {
				logger.error("=====>参数不能为空,或者有误");
				returnMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY, null);
			}
		} catch (Exception e) {
			returnMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null);
			e.printStackTrace();
		}
		return returnMap;
	}
	
	/**
	 * getDateMoneyByCompanyToRate 根据商户ID获取旗下的支付通道的数据统计
	 * @param map
	 * @return 设定文件 Map<String,Object> 返回类型
	 */
	@ResponseBody
	@RequestMapping("getDateMoneyByRToC")
	public Map<String, Object> getDateMoneyByRateToCompany(@RequestParam Map<String, Object> map) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try{
			boolean isNotNull = ObjectUtil.checkObject(new String[] { "rateId" }, map);
			if (isNotNull) {
				Date as = new Date();
				String time = DateUtil.DateToString(as, "yyyyMMdd");
				Long rateId = Long.valueOf(map.get("rateId").toString());
				// 获取商户旗下支持的支付通道ID
	//			map.put("isDelete", 2);
	//			map.put("payWayStatus", 1);
				map.put("rateId",rateId);
				List<Payv2BussSupportPayWay> wayList = Payv2BussSupportPayWayService.query(map);
				List<Map<String,Object>> countList = payv2PayOrderService.selectByRateCom(map);
				DateMoneyVo dateMoneyVo = null;
				List<DateMoneyVo> voList = new ArrayList<DateMoneyVo>();
				double sumDateMoney = 0.00;
				int redisType=1;
				for (Payv2BussSupportPayWay payv2BussSupportPayWay : wayList) {
					if(payv2BussSupportPayWay.getRateId()!=null){
						//String companyIdAndRateIdKey=companyId+"CID"+rateId+"RID"+time+"TYPE"+redisType;
						String companyIdAndRateIdKey = payv2BussSupportPayWay.getParentId()+"CID"+ rateId+"RID"+ time+"TYPE"+redisType;
						String companyIdAndRateValue = RedisDBUtil.redisDao.getString(companyIdAndRateIdKey);
						Map<String,Object> mapCount = getMapByRateCom(countList, payv2BussSupportPayWay.getParentId(),rateId);
						//支付成功率
						double payR= 0;
						if(mapCount!=null){
							double allCount = Double.valueOf(mapCount.get("allCount").toString());
							double payCount = Double.valueOf(mapCount.get("payCount").toString());
							if(allCount!=0){
								payR = DecimalUtil.getCeil(payCount*100/allCount,2);
							}
						}
						Map<String,Object> param = new HashMap<>();
						param.put("id", payv2BussSupportPayWay.getParentId());
						// 根据rateId获取支付通道名字
						Payv2BussCompany payv2BussCompany = payv2BussCompanyService.detail(param);
						if(payv2BussCompany==null){
							continue;
						}
						if (companyIdAndRateValue != null) {
							dateMoneyVo = new DateMoneyVo();
							dateMoneyVo.setId(payv2BussSupportPayWay.getParentId());
							dateMoneyVo.setName(payv2BussCompany.getCompanyName());
							BigDecimal b = new BigDecimal(companyIdAndRateValue);
							double money = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							dateMoneyVo.setMoney(money);
							double companyIdAndRateMoney = Double.valueOf(companyIdAndRateValue);
							dateMoneyVo.setPayR(payR);
							// 某商户的总数据统计
							sumDateMoney = sumDateMoney + companyIdAndRateMoney;
							voList.add(dateMoneyVo);
						} else {
							dateMoneyVo = new DateMoneyVo();
							dateMoneyVo.setId(payv2BussSupportPayWay.getParentId());
							dateMoneyVo.setName(payv2BussCompany.getCompanyName());
							dateMoneyVo.setMoney(0.00);
							dateMoneyVo.setPayR(payR);
							voList.add(dateMoneyVo);
						}
					}
				}
				BigDecimal b = new BigDecimal(sumDateMoney);
				sumDateMoney = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				returnMap.put("sumDateMoney", sumDateMoney);
				returnMap.put("dateTime", DateUtil.DateToString(as, "yyyy-MM-dd HH:mm:ss"));
				if(voList.size()>0){
					ListSort(voList);
				}
				returnMap.put("dateSumList", voList);
				returnMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, returnMap);
			} else {
				logger.error("=====>参数不能为空,或者有误");
				returnMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY, null);
			}
		} catch (Exception e) {
			returnMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null);
			e.printStackTrace();
		}
		return returnMap;
	}
	
	private void ListSort(List<DateMoneyVo> list){
		Collections.sort(list, new Comparator<DateMoneyVo>() {  
			@Override
            public int compare(DateMoneyVo o1, DateMoneyVo o2) {  
            	try {
            		if (o1.getMoney().doubleValue()> o2.getMoney().doubleValue()) {
						return -1;
					} else if (o1.getMoney().doubleValue()< o2.getMoney().doubleValue()) {
						return 1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
            	return 0;
            }  
        }); 
	}
	
	/**
	 * @throws Exception
	 * @Title: callbackOrder
	 * @Description:回调支付接口
	 * @param map
	 * @return 设定文件
	 * @return Map<String,Object> 返回类型
	 * @date 2016年12月21日 上午11:48:38
	 * @throws
	 */
	@ResponseBody
	@RequestMapping("getRateList")
	public Map<String, Object> getRateList(@RequestParam Map<String, Object> map) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
//		map.put("status", 1);
//		map.put("isDelete", 2);
		List<Payv2PayWayRate> list = payv2PayWayRateService.query(map);
		returnMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, list);
		return returnMap;
	}
	
	@ResponseBody
	@RequestMapping("getCompanyList")
	public Map<String, Object> getCompanyList(@RequestParam Map<String, Object> map) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
//		map.put("status", 1);
//		map.put("isDelete", 2);
		List<Payv2BussCompany> list = payv2BussCompanyService.query(map);
		returnMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, list);
		return returnMap;
	}
	
	private Map<String,Object> getMapByRate(List<Map<String,Object>> list,Long rateId){
		if(list!=null){
			for (Map<String, Object> map : list) {
				if(map.containsKey("rate_id")){
					Long rate_id = Long.valueOf(map.get("rate_id").toString());
					if(rate_id.longValue()==rateId.longValue()){
						return map;
					}
				}
			}
		}
		return null;
	}
	private Map<String,Object> getMapByCom(List<Map<String,Object>> list,Long companyId){
		if(list!=null){
			for (Map<String, Object> map : list) {
				if(map.containsKey("company_id")){
					Long company_id = Long.valueOf(map.get("company_id").toString());
					if(company_id.longValue()==companyId.longValue()){
						return map;
					}
				}
			}
		}
		return null;
	}
	private Map<String,Object> getMapByRateCom(List<Map<String,Object>> list,Long companyId,Long rateId){
		if(list!=null){
			for (Map<String, Object> map : list) {
				if(map.containsKey("company_id")&&map.containsKey("rate_id")){
					Long company_id = Long.valueOf(map.get("company_id").toString());
					Long rate_id = Long.valueOf(map.get("rate_id").toString());
					if(company_id.longValue()==companyId.longValue()
							&&rate_id.longValue()==rateId.longValue()){
						return map;
					}
				}
			}
		}
		return null;
	}
	
	@ResponseBody
	@RequestMapping("test")
	public Map<String, Object> test() {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<DateMoneyVo> voList = new ArrayList<DateMoneyVo>();
		double payR = DecimalUtil.getCeil(DecimalUtil.mul(DecimalUtil.div(121.0
				,166.0, 10), 100),2);
		DateMoneyVo dateMoneyVo = new DateMoneyVo();
		dateMoneyVo.setPayR(payR);
		voList.add(dateMoneyVo);
		returnMap.put("dateSumList", voList);
		returnMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, returnMap);
		return returnMap;

	}
	/**
	 * 获取订单回调信息列表
	 * 
	 * @param map
	 * @return
	 */	
	@RequestMapping("callBackInfo")
	public ModelAndView callBackInfo(@RequestParam Map<String,Object> map) {
		ModelAndView mv = new ModelAndView("payv2/order/payv2PayOrder_callback_info");
		// 获取回调信息列表
		map.put("sortName", "call_time");
		map.put("sortOrder", "DESC");
		PageObject<Payv2PayOrderNotify> callBackPage = payv2PayOrderNotifyService.Pagequery(map);
		mv.addObject("list",callBackPage);
		mv.addObject("map",map);
		return mv;
	}
	
	/**
	 * 手动回调商户
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping("callBackCom")
	public Map<String, Object> callBackCom(@RequestParam Map<String,Object> map) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		// 回调商户，返回TRUE或FALSE
		Payv2PayOrder order = new Payv2PayOrder();
		Payv2BussCompanyApp pbca = null;
		Payv2BussCompany pbc = null;
		boolean bool = false;
		try {
			order.setOrderNum(map.get("orderNum").toString());
			order = payv2PayOrderService.selectSingle(order);
			if (order != null && order.getPayStatus() != PayFinalUtil.PAY_ORDER_SUCCESS) {
				if(order.getAppId()!=null){
					// 查询商家app 获取回调地址
					pbca = new Payv2BussCompanyApp();
					pbca.setId(order.getAppId());
					pbca = payv2BussCompanyAppService.selectSingle(pbca);					
				}else{
					pbc = new Payv2BussCompany();
					pbc.setId(order.getCompanyId());
					pbc = payv2BussCompanyService.selectSingle(pbc);					
				}
				bool = payv2PayOrderService.callBackCom(order, pbca, pbc);
			}
			if(bool){
				returnMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null,"回调成功");
			}else{
				returnMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null,"回调失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null,"服务器异常，请联系技术！");
		}
		return returnMap;
	}
	
	public static void main(String[] args) {
		Date as = new Date();
		String time = DateUtil.DateToString(as, "yyyyMMdd");
		int redisType=1;
		Long rateId=13l;
		Long companyId=205l;
		String rateRedisKey=rateId+"RID"+time+"TYPE"+redisType;
		String companyRedisKey1=companyId+"CID"+time+"TYPE"+redisType;
		String companyIdAndRateIdKey=companyId+"CID"+rateId+"RID"+time+"TYPE"+redisType;
//		RedisDBUtil.redisDao.del(time);
		RedisDBUtil.redisDao.setString(rateRedisKey,"6666.00", 86405);
		RedisDBUtil.redisDao.setString(companyRedisKey1,"6666.00", 86405);
		RedisDBUtil.redisDao.setString(companyIdAndRateIdKey,"6666.00", 86405);
		String companyIdAndRateValue = RedisDBUtil.redisDao.getString(companyIdAndRateIdKey);
		System.out.println(companyIdAndRateValue);
	}
}
