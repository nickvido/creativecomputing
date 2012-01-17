/* $Id: vertexpassthrough.glshader,v 1.1 2007/03/08 21:23:34 dyken Exp $ */

varying vec3 indir;
varying vec2 ininfo;

void main()
{
	indir = gl_Normal;
	ininfo = gl_MultiTexCoord0.xy;
	gl_Position = gl_Vertex;
}
