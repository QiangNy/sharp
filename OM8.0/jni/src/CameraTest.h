#ifndef _CAMERATEST_
#define _CAMERATEST_

typedef unsigned char uchar;

#define SUCCESS	(0)
#define WRONG	(-1)

#ifdef __cplusplus
extern	"C"
{
#endif

int     setMetricLabels(int* pLabels, int nUserRegions);
int		getSharpness(const uchar* pbImg, int nWidth, int nHeight, float threshold, int* result, int* px, int* py);

#ifdef __cplusplus
}
#endif

#endif //_CAMERATEST_
