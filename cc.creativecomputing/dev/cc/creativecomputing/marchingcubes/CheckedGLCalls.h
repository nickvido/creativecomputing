/* -*- mode: C++; tab-width:4; c-basic-offset: 4; indent-tabs-mode:nil -*-
 *
 * (C) 2007 Christopher Dyken, <erikd@ifi.uio.no>
 * 
 * Distributed under the GNU GPL
 *
 */
#ifndef _DYKEN_GLUTIL_CHECKED_GLCALLS_H_
#define _DYKEN_GLUTIL_CHECKED_GLCALLS_H_

#include <string>
#include <stdexcept>
#include <GL/glew.h>
//#include <GL/gl.h>
//#include <GL/glext.h>

namespace dyken {
	namespace GLUtil {

/** Exception class for OpenGL errors. */
class opengl_error : public std::runtime_error
{
public:
	opengl_error(GLenum error, char* file = "", int line = 0);

protected:
	static std::string getErrorMessage(GLenum error, char* file, int line);
};

/** Macro that checks OpenGL state */
#ifndef CHECK_GL
#define CHECK_GL \
do { \
	GLenum error = glGetError(); \
	if(error != GL_NO_ERROR) \
		throw dyken::GLUtil::opengl_error(error, __FILE__, __LINE__); \
} while (0)
#endif

GLint
getSingleInteger( GLenum pname );

void
dumpSourceWithLineNumbers( const std::string& src );

GLint
getUniformLocation( GLuint program, const std::string& name );

GLuint
buildProgram( GLuint vs, GLuint fs );

void
linkProgram( GLuint program );

void
compileShader( GLuint shader, const std::string& source );

GLuint
compileShader( const std::string& source, GLuint type );

void
checkFramebufferStatus( const std::string& where );

void
bufferData( GLenum target, GLsizeiptr size, GLenum usage, bool clear = false );

GLint
_getVaryingLocationNV(GLuint program, const char *name, const char* file, int line );


#define getVaryingLocationNV(program,name) (dyken::GLUtil::_getVaryingLocationNV((program),(name),__FILE__,__LINE__))


	} // namespace GLUtil
} // namespace dyken
#endif
