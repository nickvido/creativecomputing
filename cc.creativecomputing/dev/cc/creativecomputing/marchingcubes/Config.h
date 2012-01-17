/* -*- mode: C++; tab-width:4; c-basic-offset: 4; indent-tabs-mode:nil -*-
 *
 * (C) 2007 Christopher Dyken, <erikd@ifi.uio.no>
 * 
 * Distributed under the GNU GPL
 *
 */
#ifndef _DYKEN_MARCHING_CONFIG_H_
#define _DYKEN_MARCHING_CONFIG_H_

#ifdef _WIN32
#include <math.h>

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

static inline double log2( double val )
{
	return log(val) / log(2.0);	
}

static inline double drand48()
{
	return 0.5;
}

#ifndef DllExport
#define DllExport
#endif
#ifndef __func__
#define __func__ __FUNCTION__
#endif
#endif


#include <string>
#include <vector>
#include <GL/glew.h>
#include <GfxMath/Vec.h>
#include <GfxMath/Mat.h>


namespace dyken {
	namespace Marching {



class Config
{
public:
	

};

 // namespace dyken
#endif
