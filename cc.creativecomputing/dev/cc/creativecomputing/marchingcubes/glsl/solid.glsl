// $Id: solid.glshader,v 1.1 2007/03/23 13:50:50 dyken Exp $



varying vec3 cs_normal;


void main()
{
//	gl_FragColor = vec4(1.0);
//gl_SecondaryColor.rgb-vec3(0.5)
	gl_FragColor = gl_Color + 0.00001*cs_normal.xyzy;
}
