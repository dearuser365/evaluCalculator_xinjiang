package com.kd.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kd.business.calculator.CalculatorInterface;
import com.kd.business.calculator.DistrictCalculator;
import com.kd.business.calculator.ResultCodeTableDistinctor;
import com.kd.business.calculator.ScoreCalculator;
import com.kd.business.detailInsertor.DetailInsertorInterface;
import com.kd.business.loader.OrganLoader;
import com.kd.business.parser.dms.DmsFileInsertor;
import com.kd.business.parser.dms.DmsFileInsertorInterface;
import com.kd.business.parser.pms.PmsFileInsertor;
import com.kd.business.tableCreator.TableCreator;
import com.kd.business.task.CalculateTask;
import com.kd.business.unevalu.UnevaluCalculator;
import com.kd.entity.Organ;
import com.kd.service.SystemService;
import com.kd.util.AjaxJson;
import com.kd.util.DateUtil;
import com.kd.util.FileParseUtilTool;
import com.kd.util.PropertiesUtil;

@Controller
@RequestMapping("reInsertFileController")
public class ReInsertFileController implements ApplicationContextAware{
	
	private static final Logger logger = Logger.getLogger(ReInsertFileController.class);
	@Autowired
	TableCreator tableCreator;
	private ApplicationContext applicationContext;
	
	@Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
	
	/**
	 * 
	 * @Description: reInsert页面
	 * @param request
	 * @return
	 * @return ModelAndView
	 *
	 */
	@RequestMapping("reInsert")
	public ModelAndView reInsert(HttpServletRequest request){
		ServletContext servletContext=request.getSession().getServletContext();
		WebApplicationContext webApplicationContext = (WebApplicationContext)servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		Map<String, CalculatorInterface> calculateMap = webApplicationContext.getBeansOfType(CalculatorInterface.class);
		Map<String, DetailInsertorInterface> insertorMap = webApplicationContext.getBeansOfType(DetailInsertorInterface.class);
		
		JSONArray jarray = new JSONArray();
		CalculatorInterface ci = null;
		for(Map.Entry<String, CalculatorInterface> entry:calculateMap.entrySet()){ 
			ci = entry.getValue();
			JSONObject jo = new JSONObject();
			jo.put("name", ci.getName());
			jo.put("className", ci.getClass().getSimpleName());
			jo.put("isUnevalu", ci.isUnevalu());
			jarray.add(jo);
		}
		JSONArray insertorJarray = new JSONArray();
		DetailInsertorInterface insertor = null;
		for(Map.Entry<String, DetailInsertorInterface> entry:insertorMap.entrySet()){ 
			insertor = entry.getValue();
			JSONObject jo = new JSONObject();
			jo.put("name", insertor.getName());
			jo.put("className", insertor.getClass().getSimpleName());
			insertorJarray.add(jo);
		}
		List<Organ> organList =
		webApplicationContext.getBean(OrganLoader.class).getOrganList();
		
		request.setAttribute("calculateMap", jarray.toJSONString());
		request.setAttribute("insertorMap", insertorJarray.toJSONString());
		request.setAttribute("dateStr", DateUtil.DAY_DFM.format(DateUtil.getUserWantDate(-1)));
		request.setAttribute("monthStr", DateUtil.MONTH_DFM.format(DateUtil.getUserWantDate(-1)));
		request.setAttribute("organList", JSON.toJSONString(organList));
		return new ModelAndView("reInsert");
	}
	
	/**
	 * 
	 * @Description: 重算 
	 * @param request
	 * @return
	 * @return AjaxJson
	 *
	 */
	@RequestMapping("reCalculateAllAjax")
	@ResponseBody
	public AjaxJson reCalculateAllAjax(HttpServletRequest request){
		logger.info("reCalculateAllAjax");
		AjaxJson json = new AjaxJson();
		String dateStr = request.getParameter("dateStr");
		try {
			Date date = DateUtil.DAY_DFM.parse(dateStr);
			ServletContext servletContext=request.getSession().getServletContext();
			WebApplicationContext webApplicationContext = (WebApplicationContext)servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			webApplicationContext.getBean(CalculateTask.class).calculateIndex(date);
			json.setMsg("ok");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg(e.getMessage());
			json.setSuccess(false);
			e.printStackTrace();
		}
		
		return json;
	}
	
	@RequestMapping("calculatorNewIndexAjax")
	@ResponseBody
	public AjaxJson calculatorNewIndexAjax(HttpServletRequest request){
		logger.info("calculatorNewIndexAjax");
		AjaxJson json = new AjaxJson();
		String dateStr = request.getParameter("dateStr");
		try {
			Date date = DateUtil.DAY_DFM.parse(dateStr);
			ServletContext servletContext=request.getSession().getServletContext();
			WebApplicationContext webApplicationContext = (WebApplicationContext)servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			//webApplicationContext.getBean(CalculatorNewIndex.class).calculate(date);
			json.setMsg("ok");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg(e.getMessage());
			json.setSuccess(false);
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 单个指标计算
	 * @Description: 
	 * @param request
	 * @return
	 * @return AjaxJson
	 *
	 */
	@RequestMapping("calculatorSingleIndexAjax")
	@ResponseBody
	public AjaxJson calculatorSingleIndexAjax(HttpServletRequest request){
		logger.info("calculatorSingleIndexAjax");
		AjaxJson json = new AjaxJson();
		String dateStr = request.getParameter("dateStr");
		try {
			Date date = DateUtil.DAY_DFM.parse(dateStr);
			String className = request.getParameter("className");
			ServletContext servletContext=request.getSession().getServletContext();
			WebApplicationContext webApplicationContext = (WebApplicationContext)servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			DistrictCalculator districtCalculator 
			= webApplicationContext.getBean(DistrictCalculator.class);
			ResultCodeTableDistinctor resultCodeTableDistinctor
			= webApplicationContext.getBean(ResultCodeTableDistinctor.class);
			UnevaluCalculator unevaluCalculator
			= webApplicationContext.getBean(UnevaluCalculator.class);

			logger.info("计算指标:"+className);

			
			
			if (!"ScoreCalculator".equals(className)) {
				//重算其他指标
				((CalculatorInterface) webApplicationContext.getBean(className)).callSql(date);
				((CalculatorInterface) webApplicationContext.getBean(className)).calculateDayTable(date);
				//重置免考核指标
				unevaluCalculator.setAgreeFlag(DateUtil.DAY_DFM.format(date));
				
				((CalculatorInterface) webApplicationContext.getBean(className)).calculateMonthTable(date);	
				
				if ("DevCalculator".equals(className)) {
					//重算其他指标
					((CalculatorInterface) webApplicationContext.getBean("PDBYQWZLCalculator")).calculateDayTable(date);
					((CalculatorInterface) webApplicationContext.getBean("PDDZSWZLCalculator")).calculateDayTable(date);
					((CalculatorInterface) webApplicationContext.getBean("PDKGSWZLCalculator")).calculateDayTable(date);
					((CalculatorInterface) webApplicationContext.getBean("PDMXSWZLCalculator")).calculateDayTable(date);
					((CalculatorInterface) webApplicationContext.getBean("PDZFSWZLCalculator")).calculateDayTable(date);
					((CalculatorInterface) webApplicationContext.getBean("PDBYQWZLCalculator")).calculateMonthTable(date);
					((CalculatorInterface) webApplicationContext.getBean("PDDZSWZLCalculator")).calculateMonthTable(date);	
					((CalculatorInterface) webApplicationContext.getBean("PDKGSWZLCalculator")).calculateMonthTable(date);	
					((CalculatorInterface) webApplicationContext.getBean("PDMXSWZLCalculator")).calculateMonthTable(date);	
					((CalculatorInterface) webApplicationContext.getBean("PDZFSWZLCalculator")).calculateMonthTable(date);

					
				}
			}
			
			//重算总分
			webApplicationContext.getBean(ScoreCalculator.class).calculate(date);
			//计算地区指标
			districtCalculator.calculate(date);
			
			//最后一次去重
			resultCodeTableDistinctor.distinctCodeTable(DateUtil.getResultDayTableName(date));
			resultCodeTableDistinctor.distinctCodeTable(DateUtil.getResultMonthTableName(date));
			json.setMsg("ok");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg(e.getMessage());
			json.setSuccess(false);
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 
	 * @Description: Dms单类文件解析
	 * @param request
	 * @return
	 * @return AjaxJson
	 *
	 */
	@RequestMapping("parseSingleDmsFileAjax")
	@ResponseBody
	public AjaxJson parseSingleFile(HttpServletRequest request){
		ServletContext servletContext=request.getSession().getServletContext();
		WebApplicationContext webApplicationContext = (WebApplicationContext)servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		String datatype = request.getParameter("datatype");
		
		String type = datatype.split("@")[0];
		String className = datatype.split("@")[1];
		Properties properties = new PropertiesUtil("sysConfig.properties").getProperties();
		AjaxJson json = new AjaxJson();
		try {
			tableCreator.creatTable(new Date());
			if ("DMS".equals(type)) {
				Date currentDate = new Date();
				String currentDateStr = DateUtil.dateToStr(currentDate);
				String dmsXmlFolderStr = properties.getProperty("dmsXmlFolder");				
				String bakFolderStr = FileParseUtilTool.getDmsFtpBakFolderStr(properties, currentDateStr);
				File dmsXMLFolder = new File(dmsXmlFolderStr);
				for (File f : dmsXMLFolder.listFiles()) {
					DmsFileInsertorInterface tempInsertor = ((DmsFileInsertorInterface)webApplicationContext.getBean(className));
					if (tempInsertor.isMyFile(f.getName())) {
						tempInsertor.insertToDB(f, bakFolderStr);
					}
				}
			}
//			else if ("ZIP".equals(type)) {
//				Date currentDate = new Date();
//				String currentDateStr = DateUtil.dateToStr(currentDate);
//				String dmsZipFolderStr = properties.getProperty("dmsZipFolder");				
//				String bakFolderStr = FileParseUtilTool.getDmsFtpBakFolderStr(properties, currentDateStr);
//				File dmsZIPFolder = new File(dmsZipFolderStr);
//				for (File f : dmsZIPFolder.listFiles()) {
//					DmsFileInsertorInterface tempInsertor = ((DmsFileInsertorInterface)webApplicationContext.getBean(className));
//					if (tempInsertor.isMyFile(f.getName())) {
//						tempInsertor.insertToDB(f, bakFolderStr);
//					}
//				}
//			}
			json.setMsg("ok");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg(e.getMessage());
			json.setSuccess(false);
			e.printStackTrace();
		}

		return json;
	}
	
	/**
	 * 
	 * @Description: Pms台账入库
	 * @param request
	 * @return
	 * @return AjaxJson
	 *
	 */
	@RequestMapping("parseGpmsFileAjax")
	@ResponseBody
	public AjaxJson parseGpmsFileAjax(HttpServletRequest request){
		AjaxJson json = new AjaxJson();
		try {
			ServletContext servletContext=request.getSession().getServletContext();
			WebApplicationContext webApplicationContext = (WebApplicationContext)servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			PmsFileInsertor insertor = ((PmsFileInsertor)webApplicationContext.getBean(PmsFileInsertor.class));
			insertor.parseAndInsertDB(new Date());
			json.setMsg("ok");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg(e.getMessage());
			json.setSuccess(false);
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 
	 * @Description: 全部Dms文件解析
	 * @param request
	 * @return
	 * @return AjaxJson
	 *
	 */
	@RequestMapping("parseDmsFileAjax")
	@ResponseBody
	public AjaxJson parseDmsFileAjax(HttpServletRequest request){
		AjaxJson json = new AjaxJson();
		try {
			ServletContext servletContext=request.getSession().getServletContext();
			WebApplicationContext webApplicationContext = (WebApplicationContext)servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			DmsFileInsertor insertor = ((DmsFileInsertor)webApplicationContext.getBean(DmsFileInsertor.class));
			insertor.parseAndInsertDB();
			json.setMsg("ok");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg(e.getMessage());
			json.setSuccess(false);
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 
	 * @Description: Dms单类明细入库
	 * @param request
	 * @return
	 * @return AjaxJson
	 *
	 */
	@RequestMapping("insertSingleDetailAjax")
	@ResponseBody
	public AjaxJson insertSingleDetailAjax(HttpServletRequest request){
		AjaxJson json = new AjaxJson();
		String dateStr = request.getParameter("dateStr");
		String className = request.getParameter("className");
		try {
			Date date = DateUtil.DAY_DFM.parse(dateStr);
			logger.info("插入明细"+className);
			ServletContext servletContext = request.getSession().getServletContext();
			WebApplicationContext webApplicationContext = (WebApplicationContext)servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			((DetailInsertorInterface) webApplicationContext.getBean(className)).insertDetail(date);
			json.setMsg("ok");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg(e.getMessage());
			json.setSuccess(false);
			e.printStackTrace();
		}
		
		logger.info("插入明细"+className);
		
		return json;
	}
	
	/**
	 * 
	 * @Description: 设置/取消免考核
	 * @param request
	 * @param response
	 * @return
	 * @return AjaxJson
	 *
	 */
	@RequestMapping("setFlagAjax")
	@ResponseBody
	public AjaxJson setFlagAjax(HttpServletRequest request,HttpServletResponse response){
		logger.info("setFlagAjax");
		AjaxJson json = new AjaxJson();
		SystemService service = applicationContext.getBean(SystemService.class);
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		
		String organCode = request.getParameter("organCode");
		String code = request.getParameter("code");
		String kxIds = request.getParameter("kxIds");
		String ypIds = request.getParameter("ypIds");
		String flag = request.getParameter("flag");
		String codename = request.getParameter("codename");
		
		String sql = "select count(*) from EVALUSYSTEM.\"PUBLIC\".UNEVALU  where 1= 1  and applicant_type = 'dsuneval' ";
		List<String> dates = DateUtil.getDates(startDate, endDate);
		String msg = "";
		try{
			ServletContext servletContext=request.getSession().getServletContext();
			WebApplicationContext webApplicationContext = (WebApplicationContext)servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			List<Organ> organList =
					webApplicationContext.getBean(OrganLoader.class).getOrganList();
			if (!"all".equals(organCode)) {
				for (String dateStr : dates) {
					msg = "";
					sql += " and unevalu_date ='"+DateUtil.transEvaluToDbDate(dateStr)+"'";
					sql += " and organ_code ="+organCode;
					sql += " and unevalu_code = '"+code+"'";
					long unevalInfoNum = service.getCountForJdbc(sql);
					if(unevalInfoNum > 0){
						String organname = "";
						for (Organ organ : organList) {
							if(organCode.equals(organ.getCode())){
								organname = organ.getName();
							}
						}
						msg = DateUtil.transEvaluToDbDate(dateStr)+","+codename+","+organname	+"的指标免考核已经申请过，请不要重复申请！";
						json.setSuccess(true);
						json.setMsg(msg);
						return json;
					}
					
				}
			}else{
				sql = "select * from EVALUSYSTEM.\"PUBLIC\".UNEVALU  where 1= 1 and applicant_type = 'dsuneval' ";
				for (String dateStr : dates) {
					msg = "";
					sql += " and unevalu_date ='"+DateUtil.transEvaluToDbDate(dateStr)+"'";
					sql += " and unevalu_code = '"+code+"'";
					List<Map<String,Object>> list = service.findForJdbc(sql);
					if(!list.isEmpty() && list.size() > 0){
						for(Map<String,Object> map : list){
							String organ_code = String.valueOf(map.get("ORGAN_CODE"));
							String organname = "";
							for (Organ organ : organList) {
								if(organ_code.equals(organ.getCode())){
									organname = organ.getName();
								}
							}
							
							msg = DateUtil.transEvaluToDbDate(dateStr)+","+codename+","+organname	+"的指标免考核已经申请过，请不要重复申请！";
							json.setSuccess(true);
							json.setMsg(msg);
							return json;
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ServletContext servletContext=request.getSession().getServletContext();
			WebApplicationContext webApplicationContext = (WebApplicationContext)servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			
			if (!"all".equals(organCode)) {
				for (String dateStr : dates) {
					webApplicationContext.getBean(UnevaluCalculator.class)
					.setFlag(dateStr, organCode, code, kxIds, ypIds, flag);
				}
			} else {
				List<Organ> organList =
						webApplicationContext.getBean(OrganLoader.class).getOrganList();
				for (Organ organ : organList) {
					logger.info("置位"+organ.getName()+"免考核FLAG");
					for (String dateStr : dates) {
						webApplicationContext.getBean(UnevaluCalculator.class)
						.setFlag(dateStr, organ.getCode(), code, kxIds, ypIds, flag);
					}
				}
				
			}
			
			json.setMsg("置位免考核FLAG成功！");
			json.setSuccess(true);
			logger.info("置位免考核FLAG成功！");
		} catch (Exception e) {
			json.setMsg(e.getMessage());
			json.setSuccess(false);
			e.printStackTrace();
		}
	
		
		return json;
	}
	
	/**
	 * 
	 * @Description: 计算免考核
	 * @param request
	 * @return
	 * @return AjaxJson
	 *
	 */
	@RequestMapping("calculatorUnevaluAjax")
	@ResponseBody
	public AjaxJson calculatorUnevaluAjax(HttpServletRequest request){
		logger.info("calculatorUnevaluAjax");
		AjaxJson json = new AjaxJson();
		String dateStr = request.getParameter("dateStr");
		try {
			Date date = DateUtil.MONTH_DFM.parse(dateStr);
			ServletContext servletContext=request.getSession().getServletContext();
			
			String className = request.getParameter("className");
			
			WebApplicationContext webApplicationContext = (WebApplicationContext)servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			
			logger.info("计算指标:"+className);
			
			webApplicationContext.getBean(UnevaluCalculator.class).calculateUnevaluByMonth(date,className);
			
			json.setMsg("ok");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg(e.getMessage());
			json.setSuccess(false);
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 
	 * @Description: 调用拓扑校验
	 * @param request
	 * @return
	 * @return AjaxJson
	 *
	 */
	@RequestMapping("checkTopoAjax")
	@ResponseBody
	public AjaxJson checkTopoAjax(HttpServletRequest request){
		logger.info("checkTopoAjax");
		AjaxJson json = new AjaxJson();
		String dateStr = request.getParameter("dateStr");
		
		logger.info("开始调用模型校验程序");
		long startTime = System.currentTimeMillis();
		String statistime = dateStr.replace("-", "");
		logger.info("开始调用拓扑检验程序");
		
		String result = localRunCmd("/home/d5000/fujian/src/bin/modelmerge/file_scan 1 "+
				statistime+" > testmodel.log ");
		logger.info("调用拓扑校验程序返回结果是："+result);
		logger.info("今日调用模型校验程序完成，耗时 "+(System.currentTimeMillis() - startTime)+" 毫秒");
		
		json.setMsg("ok");
		json.setSuccess(true);
		
		return json;
	}
	
	
	public static String localRunCmd(String cmd) {
		Process myProc = null;
		BufferedReader in = null;
		StringBuilder sb = new StringBuilder();
		try {
			logger.info(cmd);
			myProc = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
			myProc.waitFor();
			in = new BufferedReader(new InputStreamReader(
					myProc.getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
				if (line.trim().length() > 0) {
					sb.append(line+"\n");
				}
			}
			return sb.toString();
		} catch (Exception e) {
			return "-1";
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
