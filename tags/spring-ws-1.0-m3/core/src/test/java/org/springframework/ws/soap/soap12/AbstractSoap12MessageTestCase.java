/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ws.soap.soap12;

import java.io.ByteArrayOutputStream;

import junit.framework.Assert;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.ws.soap.AbstractSoapMessageTestCase;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.transport.StubTransportOutputStream;

public abstract class AbstractSoap12MessageTestCase extends AbstractSoapMessageTestCase {

    public void testGetVersion() throws Exception {
        Assert.assertEquals("Invalid SOAP version", SoapVersion.SOAP_12, soapMessage.getVersion());
    }

    public void testWriteTo() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        soapMessage.writeTo(outputStream);
        assertXMLEqual("<Envelope xmlns='http://www.w3.org/2003/05/soap-envelope'><Header/><Body/></Envelope>",
                new String(outputStream.toByteArray(), "UTF-8"));
    }

    protected final Resource[] getSoapSchemas() {
        return new Resource[]{new ClassPathResource("xml.xsd", AbstractSoap12MessageTestCase.class),
                new ClassPathResource("soap12.xsd", AbstractSoap12MessageTestCase.class)};
    }

    public void testWriteToTransportResponse() throws Exception {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StubTransportOutputStream tos = new StubTransportOutputStream(bos);
        soapMessage.writeTo(tos);
        String result = bos.toString("UTF-8");

        assertXMLEqual("<Envelope xmlns='http://www.w3.org/2003/05/soap-envelope'><Header/><Body/></Envelope>", result);
        String contentType = (String) tos.getHeaders().get("Content-Type");
        assertTrue("Invalid Content-Type set", contentType.indexOf(SoapVersion.SOAP_12.getContentType()) != -1);
    }

    public void testWriteToTransportResponseAttachment() throws Exception {
        InputStreamSource inputStreamSource = new ByteArrayResource("contents".getBytes("UTF-8"));
        soapMessage.addAttachment(inputStreamSource, "text/plain");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StubTransportOutputStream tos = new StubTransportOutputStream(bos);
        soapMessage.writeTo(tos);
        String contentType = (String) tos.getHeaders().get("Content-Type");
        assertTrue("Invalid Content-Type set", contentType.indexOf(SoapVersion.SOAP_12.getContentType()) != -1);
    }


}