/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package org.dlese.dpc.schemedit.ndr.util.integration;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

class MisMatch {
	
	public String id;
	public Result ncsResult;
	public Result nsdlResult;
	
	public MisMatch (String id, Result ncsResult, Result nsdlResult) {
		this.id = id;
		this.ncsResult = ncsResult;
		this.nsdlResult = nsdlResult;
	}
	
	public MisMatch (Element e) {
		this.id = e.attributeValue("ncsrecordid");
		this.ncsResult = new Result (e.element("ncs"));
		this.nsdlResult =  new Result (e.element("nsdl"));
	}
	
	public String toString () {
		String s = "MisMatch: " + id;
		if (ncsResult != null) {
			s += "\n\t ncs  url: " + this.ncsResult.resourceUrl +
				 "  handle: " + this.ncsResult.resourceHandle;
		}
		if (nsdlResult != null) {
			s += "\n\t nsdl  url: " + this.nsdlResult.resourceUrl +
				 "  handle: " + this.nsdlResult.resourceHandle;
		}
		return s;
	}
	
	public Element asElement () {
		Element e = DocumentHelper.createElement ("mismatch");
		e.addAttribute ("ncsrecordid", id);
		e.add (this.ncsResult.asElement("ncs"));
		e.add (this.nsdlResult.asElement("nsdl"));
		return e;
	}
}

