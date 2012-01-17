uniform samplerRECT input;

void main(
	in 		float2 		coords	: WPOS,       
	    
	out 	float4 		output0 : COLOR0,
	out 	float4 		output1 : COLOR1,    
	out 	float4 		output2 : COLOR2,    
	out 	float4 		output3 : COLOR3
) { 
	float4 value = texRECT(input, coords);   
	output0 = value;//noise(value);                        
	output1 = value * 2;//noise(value);                   
	output2 = -value;                  
	output3 = value / 100;
}               