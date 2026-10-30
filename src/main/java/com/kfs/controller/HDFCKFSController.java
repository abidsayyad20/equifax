package com.kfs.controller;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kfs.service.HdfcKFSService;



@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = { "/kfs" })
public class HDFCKFSController {
	
	@Autowired
	HdfcKFSService hdfcKFSService;
	
	@RequestMapping(value = "/downloadhdfckfs", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<InputStreamResource> downloadKFS()
			throws Exception {


		byte[] fileContent = hdfcKFSService.generateHDFCReport();

		if (fileContent == null || fileContent.length == 0) {
			return ResponseEntity.noContent().build();

		}
		ByteArrayInputStream bdata = new ByteArrayInputStream(fileContent);

		HttpHeaders objHeader = new HttpHeaders();

		objHeader.add("Content-Disposition", "attachment; filename=" + "HDFC_KFS.pdf");

		return ResponseEntity.ok().headers(objHeader).body(new InputStreamResource(bdata));
	}

}
