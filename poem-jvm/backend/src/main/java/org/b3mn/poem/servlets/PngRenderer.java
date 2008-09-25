package org.b3mn.poem.servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class PngRenderer extends ImageRenderer {
	
	@Override
    protected void setResponseHeaders(HttpServletResponse res) {
  		res.setContentType("image/png");
  		res.setStatus(200);
    }

    @Override
    protected void transcode(String in_s, OutputStream out) throws TranscoderException, IOException {
    	InputStream in = new ByteArrayInputStream(in_s.getBytes());
	  	PNGTranscoder transcoder = new PNGTranscoder();
	  	try {
	    	TranscoderInput input = new TranscoderInput(in);

	    	// Setup output
	    	out = new java.io.BufferedOutputStream(out);
	    	try {
		    	TranscoderOutput output = new TranscoderOutput(out);
		    	// Do the transformation
				transcoder.transcode(input, output);
	    	} finally {
				out.close();
	    	}
	  	} finally {
	    	in.close();
		}
    }

}