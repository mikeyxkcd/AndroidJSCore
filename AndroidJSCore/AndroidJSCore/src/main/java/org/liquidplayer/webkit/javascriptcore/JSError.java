//
// JSError.java
// AndroidJSCore project
//
// https://github.com/ericwlange/AndroidJSCore/
//
// Created by Eric Lange
//
/*
 Copyright (c) 2014-2016 Eric Lange. All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 - Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 - Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.liquidplayer.webkit.javascriptcore;

/**
 * A convenience class for managing JavaScript error objects
 * @since 1.0
 */
public class JSError extends JSObject {
	/**
	 * Generates a JavaScript throwable exception object
	 * @param ctx  The context in which to create the error
	 * @param message  The description of the error
	 * @param filename   The name of the file in which the error occurred. This is used for stack
	 *                   tracing and is optional.
	 * @param lineNumber  The line number where the error occurred. This is used for stack tracing
	 *                    and is optional.
	 * @since 1.0
	 * @throws JSException
	 */
	public JSError(JSContext ctx, String message, String filename, Integer lineNumber) throws JSException {
		context = ctx;
		long [] args = { 
				new JSValue(context,message).valueRef(),
				new JSValue(context,filename).valueRef(),
				new JSValue(context,lineNumber).valueRef()
		};
		JNIReturnObject jni = makeError(context.ctxRef(), args);
		if (jni.exception!=0) {
			context.throwJSException(new JSException(new JSValue(jni.exception, context)));
			jni.reference = make(context.ctxRef(), 0L);
		}
		valueRef = jni.reference;
	}
	/**
	 * Generates a JavaScript throwable exception object
	 * @param ctx  The context in which to create the error
	 * @param message  The description of the error
	 * @since 1.0
	 * @throws JSException
	 */
	public JSError(JSContext ctx, String message) throws JSException {
		context = ctx;
		long [] args = { 
				new JSValue(context,message).valueRef()
		};
		JNIReturnObject jni = makeError(context.ctxRef(), args);
		if (jni.exception!=0) {
			context.throwJSException(new JSException(new JSValue(jni.exception, context)));
			jni.reference = make(context.ctxRef(), 0L);
		}
		valueRef = jni.reference;
	}
	/**
	 * Generates a JavaScript throwable exception object
	 * @param ctx  The context in which to create the error
	 * @since 1.0
	 * @throws JSException
	 */
	public JSError(JSContext ctx) throws JSException {
		context = ctx;
		JNIReturnObject jni = makeError(context.ctxRef(), new long[0]);
		if (jni.exception!=0) {
			context.throwJSException(new JSException(new JSValue(jni.exception, context)));
			jni.reference = make(context.ctxRef(), 0L);
		}
		valueRef = jni.reference;
	}
}
