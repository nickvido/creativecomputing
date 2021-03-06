//----------------------------------------------------------------------------
// File : floUtil.cg
//----------------------------------------------------------------------------
// Copyright 2003 Mark J. Harris and
// The University of North Carolina at Chapel Hill
//----------------------------------------------------------------------------
// Permission to use, copy, modify, distribute and sell this software and its 
// documentation for any purpose is hereby granted without fee, provided that 
// the above copyright notice appear in all copies and that both that copyright 
// notice and this permission notice appear in supporting documentation. 
// Binaries may be compiled with this software without any royalties or 
// restrictions. 
//
// The author(s) and The University of North Carolina at Chapel Hill make no 
// representations about the suitability of this software for any purpose. 
// It is provided "as is" without express or implied warranty.
/**
 * @file floUtil.cg
 * 
 * This file contains useful routines used by multiple programs in Flo.
 */

// basic vertex-fragment connectors.

struct fvfFlo
{
  float4 TEX0;
  float4 TEX1;
  float4 WPOS;
};

struct hvfFlo
{
  half4 TEX0;
  half4 TEX1;
  half4 WPOS;
};

void h4texRECTneighbors(samplerRECT tex, half2 s,
                        out half4 left,
                        out half4 right,
                        out half4 bottom,
                        out half4 top)
{
  left   = h4texRECT(tex, s - half2(1, 0)); 
  right  = h4texRECT(tex, s + half2(1, 0));
  bottom = h4texRECT(tex, s - half2(0, 1));
  top    = h4texRECT(tex, s + half2(0, 1));
}

void h1texRECTneighbors(samplerRECT tex, half2 s,
                        out half left,
                        out half right,
                        out half bottom,
                        out half top)
{
  left   = h1texRECT(tex, s - half2(1, 0)); 
  right  = h1texRECT(tex, s + half2(1, 0));
  bottom = h1texRECT(tex, s - half2(0, 1));
  top    = h1texRECT(tex, s + half2(0, 1));
}


void f4texRECTneighbors(samplerRECT tex, float2 s,
                        out float4 left,
                        out float4 right,
                        out float4 bottom,
                        out float4 top)
{
  left   = f4texRECT(tex, s - float2(1, 0)); 
  right  = f4texRECT(tex, s + float2(1, 0));
  bottom = f4texRECT(tex, s - float2(0, 1));
  top    = f4texRECT(tex, s + float2(0, 1));
}

void f1texRECTneighbors(samplerRECT tex, float2 s,
                        out float left,
                        out float right,
                        out float bottom,
                        out float top)
{
  left   = f1texRECT(tex, s - float2(1, 0)); 
  right  = f1texRECT(tex, s + float2(1, 0));
  bottom = f1texRECT(tex, s - float2(0, 1));
  top    = f1texRECT(tex, s + float2(0, 1));
}


//----------------------------------------------------------------------------
// Function     	  : f4texRECTbilerp
// Description	    : 
//----------------------------------------------------------------------------


