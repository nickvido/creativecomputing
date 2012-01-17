/* -*- mode: C++; tab-width:4; c-basic-offset: 4; indent-tabs-mode:nil -*-
 *
 * (C) 2007 Christopher Dyken, <erikd@ifi.uio.no>
 * 
 * Distributed under the GNU GPL
 *
 */
#include <cstdlib>
#include <cstring>
#include <iostream>
#include <sstream>
#include <iomanip>
#include <vector>
#include "CheckedGLCalls.h"


namespace dyken {
	namespace GLUtil {
		using std::string;

opengl_error::opengl_error(GLenum error, char* file, int line)
: std::runtime_error(getErrorMessage(error, file, line))
{ }

std::string
opengl_error::getErrorMessage(GLenum error, char* file, int line)
{
	char *str_enum = "Unknown error";
	char *str_desc = "None";
	char *str_hint = "None";

	switch(error) {
	case GL_INVALID_ENUM:
		str_enum = "GL_INVALID_ENUM";
		str_desc = "enum argument out of range. Offending command ignored.";
		str_hint = "You probably tried to pass an enum value that is not "
				   "specified as allowed for that command. ";
		break;
	case GL_INVALID_VALUE:
		str_enum = "GL_INVALID_VALUE";
		str_desc = "Numeric argument out of range. Offending command ignored.";
		str_hint = "E.g. this error is generated when you pass a negative "
				   "value to a command that takes a sizei as parameter.";
		break;
	case GL_INVALID_OPERATION:
		str_enum = "GL_INVALID_OPERATION";
		str_desc = "Operation is illegal in current state. Offending command ignored.";
		str_hint = "Some commands are not allowed between glBegin()/glEnd() pairs.";
		break;
	case GL_STACK_OVERFLOW:
		str_enum = "GL_STACK_OVERFLOW";
		str_desc = "Command would cause a stack overflow. Offending command ignored.";
		str_hint = "Calling glPushMatrix()/glPushAttrib()/glPushClientAttrib() "
				   "to a full matrix causes this error.";
		break;
	case GL_STACK_UNDERFLOW:
		str_enum = "GL_STACK_UNDERFLOW";
		str_desc = "Command would cause a stack underflow. Offending command ignored.";
		str_hint = "Calling glPopMatrix()/glPopAttrib()/glPopClientAttrib() "
				   "to an empty matrix causes this error.";
		break;
	
	case GL_OUT_OF_MEMORY:
		str_enum = "GL_OUT_OF_MEMORY";
		str_desc = "Not enough memory left to execute command.";
		str_hint = "If memory is exhausted as a side-effect of the execution of "
				   "a command, this error may be generated. I.e., it is not definite "
				   "whether or not the command was ignored or not.";
		break;
	case GL_TABLE_TOO_LARGE:
		str_enum = "GL_TABLE_TOO_LARGE";
		str_desc = "The specified table is too large. Offending command ignored.";
		break;
	}
	std::ostringstream out;
	out << str_enum;
	out << "\n** Where:       " << file << " at line " << line;
	out << "\n** Description: " << str_desc;
	out << "\n** Hint:        " << str_hint;
	return out.str();
}

GLint
getSingleInteger( GLenum pname )
{
	GLint ret;
	glGetIntegerv( pname, &ret );
	return ret;
}

void
dumpSourceWithLineNumbers( const std::string& src )
{
	int line = 1;


	for( std::string::const_iterator it = src.begin(); it!=src.end(); ++it )
	{
		std::string::const_iterator jt = it;
		int c=0;
		std::cerr << std::setw(3) << line << ": ";
		for(; *jt != '\n' && jt != src.end(); jt++) {
			if(*jt == '\t') {
				int cn = 4*(c/4+1);
				while( c < cn ) {
					std::cerr << ' ';
					c++;
				}
			} else {
				std::cerr << *jt;
				c++;
			}
		}
		std::cerr << "\n";
		line ++;
		it = jt;
		if(jt == src.end() )
			break;
	}
//	std::cerr << src << "\n";
/*
	int line = 1;
	
	for( std::string::const_iterator it=src.begin(); it!=src.end(); ++it) {
		std::string::const_iterator s = it;
		while( s != src.end() && *s != '\n' ) ++s;
		std::cerr << line << ": " << string(it, s) << "\n";
		line ++;
		it = s;
	}
*/
}


GLint
getUniformLocation( GLuint program, const std::string& name )
{
	GLint ret = glGetUniformLocation( program, name.c_str() );
	if(ret == -1 )
		std::cerr << "WARNING: Failed to locate uniform '" << name << "'\n";
	return ret;
}

GLuint
buildProgram( GLuint vs, GLuint fs )
{
	GLuint prog = glCreateProgram();
	glAttachShader( prog, vs );
	glAttachShader( prog, fs );
	linkProgram( prog );
	return prog;
}

void
linkProgram( GLuint program )
{
	glLinkProgram( program );
	GLint linkstatus;
	glGetProgramiv( program, GL_LINK_STATUS, &linkstatus );
	if( linkstatus != GL_TRUE ) {
		std::cerr << "*** Link error ***\n";
		GLint logsize;
		glGetProgramiv( program, GL_INFO_LOG_LENGTH, &logsize );
		if( logsize > 0 ) {
			std::vector<char> infolog( logsize+1);
			glGetProgramInfoLog( program, logsize, NULL, &infolog[0] );
			std::cerr << std::string( infolog.begin(), infolog.end() ) << "\n";
		} else {
			std::cerr << "empty log.\n";
		}
		throw std::runtime_error( "Failed to link program." );
	}
}

void
compileShader( GLuint shader, const std::string& source )
{
	const char* p = source.c_str();
	glShaderSource( shader, 1, &p, NULL );
	glCompileShader( shader );

	GLint status;
	glGetShaderiv( shader, GL_COMPILE_STATUS , &status );
	if(status != GL_TRUE ) {
		std::cerr << "*** Source ***\n";
		std::cerr << source << "\n";
		std::cerr << "*** Compile error ***\n";
		GLint logsize;
		glGetShaderiv( shader, GL_INFO_LOG_LENGTH, &logsize );
		if(logsize > 0 ) {
			std::vector<char> infolog( logsize+1);
//			char infolog[ logsize+1 ];
			glGetShaderInfoLog( shader, logsize, NULL, &infolog[0] );
			std::cerr << std::string( infolog.begin(), infolog.end() ) << "\n";
		} else {
			std::cerr << "empty log.\n";
		}
		throw std::runtime_error( "Failed to compile shader." );
	}
}

GLuint
compileShader( const std::string& source, GLuint type )
{
	GLuint shader = glCreateShader( type );

	const char* p = source.c_str();
	glShaderSource( shader, 1, &p, NULL );
	glCompileShader( shader );

	GLint status;
	glGetShaderiv( shader, GL_COMPILE_STATUS , &status );
	if(status != GL_TRUE ) {
		std::cerr << "*** Source ***\n";
		dumpSourceWithLineNumbers( source );
		std::cerr << "*** Compile error ***\n";
		GLint logsize;
		glGetShaderiv( shader, GL_INFO_LOG_LENGTH, &logsize );
		if(logsize > 0 ) {
			std::vector<char> infolog(logsize+1);
//			char infolog[ logsize+1 ];
			glGetShaderInfoLog( shader, logsize, NULL, &infolog[0] );
			std::cerr << std::string( infolog.begin(), infolog.end() ) << "\n";
		} else {
			std::cerr << "empty log.\n";
		}
		throw std::runtime_error( "Failed to compile shader." );
	}
	return shader;
}



#define FOO(a) case a: throw std::runtime_error( where + ": " #a ); break
void
checkFramebufferStatus( const std::string& where )
{
	GLenum status = glCheckFramebufferStatusEXT( GL_FRAMEBUFFER_EXT );
	switch( status ) {
	case GL_FRAMEBUFFER_COMPLETE_EXT:
		break;
	FOO( GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT );
	FOO( GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT );
//	FOO( GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT_EXT );
	FOO( GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT );
	FOO( GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT );
	FOO( GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT );
	FOO( GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT );
	FOO( GL_FRAMEBUFFER_UNSUPPORTED_EXT );
//	FOO( GL_FRAMEBUFFER_STATUS_ERROR_EXT );
	default:
		throw std::runtime_error( where + ": Framebuffer error.\n" );
	}
}
#undef FOO

void
bufferData( GLenum target, GLsizeiptr size, GLenum usage, bool clear )
{
	void *foo = malloc(size);
	if(clear)
		memset( foo, 0, size );
	glBufferData( target, size, foo, usage );
	free( foo );
}

GLint
_getVaryingLocationNV(GLuint program, const char* name, const char* file, int line )
{
	GLint loc = glGetVaryingLocationNV( program, name );
	if(loc == -1) {
		std::stringstream out;
		out << "glGetVaryingLocationNV( " << program << ", \"" << name << "\" ) failed at " << file << ":" << line << ".";
		throw std::runtime_error( out.str() );
	}
	return loc;
}



	} // namespace GLUtil
} // namespace dyken
