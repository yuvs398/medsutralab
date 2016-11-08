package medsutra.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import medsutra.configuration.AppConfig;
import medsutra.model.Lab;
import medsutra.services.LabService;
import medsutra.utils.AwsS3Utils;
import medsutra.utils.JSONUtils;

@Controller
public class LabController {

	@Autowired
	LabService labService;

	Gson gson;

	@Autowired
	public LabController(LabService labService, AppConfig appconfig) {
		this.labService = labService;
		this.gson = appconfig.getGson();
	}

	@RequestMapping(value = "/add/lab", method = RequestMethod.POST)

	public @ResponseBody ResponseEntity<String> labadd(@RequestPart(required = false) MultipartFile lab_pic,
			@RequestParam(required = false) String extension, @RequestParam Long city_id, @RequestParam String lab_name,
			@RequestParam String lab_reference_code, @RequestParam int pincode, @RequestParam String address,
			@RequestParam String phone, @RequestParam String servicable_pincode, @RequestParam String status,
			@RequestParam String lab_description) throws Exception {
		try {
			getLog();
			System.out.println("Uploading");

			if (!lab_pic.isEmpty()) {
				byte[] byteArr = lab_pic.getBytes();
				InputStream inputStream = new ByteArrayInputStream(byteArr);
				String pic_url = AwsS3Utils.upload(UUID.randomUUID().toString() + "." + extension, inputStream,
						extension);
				Lab lab = new Lab();
				lab.setLab_name(lab_name);
				lab.setAddress(address);
				lab.setLab_reference_code(lab_reference_code);
				lab.setServicable_pincode(servicable_pincode);
				lab.setPhone(phone);
				lab.setLab_description(lab_description);
				lab.setLab_pic(pic_url);
				lab.setStatus(status);
				lab.setPincode(pincode);
				lab.setCity_id(city_id);

				return new ResponseEntity<String>(labService.createLab(lab), HttpStatus.OK);
			} else {

				return new ResponseEntity<String>(JSONUtils.getFailJson(), HttpStatus.OK);
			}
		}

		catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	@RequestMapping(value = "/update/lab", method = RequestMethod.POST)

	public @ResponseBody ResponseEntity<String> labupdate(@RequestParam Long id,
			@RequestPart(required = false) MultipartFile lab_pic, @RequestParam(required = false) String extension,
			@RequestParam Long city_id, @RequestParam String lab_name, @RequestParam String lab_reference_code,
			@RequestParam int pincode, @RequestParam String address, @RequestParam String phone,
			@RequestParam String servicable_pincode, @RequestParam String status, @RequestParam String lab_description)
			throws Exception {
		try {
			getLog();
			System.out.println("Uploading");

			if (lab_pic != null) {
				byte[] byteArr = lab_pic.getBytes();
				InputStream inputStream = new ByteArrayInputStream(byteArr);
				String pic_url = AwsS3Utils.upload(UUID.randomUUID().toString() + "." + extension, inputStream,
						extension);
				Lab lab = new Lab();
				lab.setId(id);
				lab.setLab_name(lab_name);
				lab.setAddress(address);
				lab.setServicable_pincode(servicable_pincode);
				lab.setPhone(phone);
				lab.setLab_reference_code(lab_reference_code);
				lab.setLab_description(lab_description);
				lab.setLab_pic(pic_url);
				lab.setStatus(status);
				lab.setPincode(pincode);
				lab.setCity_id(city_id);

				return new ResponseEntity<String>(labService.updateLab(lab), HttpStatus.OK);
			} else {
				return new ResponseEntity<String>(labService.updateLabs(id, lab_name, address, servicable_pincode,
						phone, lab_description, status, pincode, city_id, lab_reference_code), HttpStatus.OK);
			}
		}

		catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	@RequestMapping(value = "/get/lab/by/all/city", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public @ResponseBody ResponseEntity<String> getLabDetails(@RequestParam String city) throws Exception {
		try {
			return new ResponseEntity<String>(labService.getLabDetails(city), HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	@RequestMapping(value = "/get/lab/by/id", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public @ResponseBody ResponseEntity<String> getLabId(@RequestParam(required = false) Long id) throws Exception {
		try {
			return new ResponseEntity<String>(labService.getLabId(id), HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	private void getLog() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		System.out.println("Api Request url = " + request.getRequestURL() + "?" + request.getQueryString());
	}
}
