/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2011-2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/

#pragma once
#include <vector>
#include <assert.h>
#include <windows.h>
namespace AppUtils
{
	const static float PRECISION = 0.000005f;
	struct int2
	{
	public: 
		int x, y;

		int2() :x(0), y(0)
		{

		}

		int2(int _x, int _y) :x(_x), y(_y)
		{

		}
		int2(const int2& i2) : x(i2.x),	y(i2.y)
		{

		}

		int2& operator=(const int2& i2)
		{
			if(this!=&i2)
			{
				x = i2.x;
				y = i2.y;
			}
			return *this;
		}

		int2 operator*(const int& iVal) const
		{
			return int2(x*iVal, y*iVal);
		}

		int dot(const int2& rhs) const
		{
			return (x * rhs.x) + (y * rhs.y);
		}
	};
	struct int4
	{
	public: 
		int x, y, z, w;

		int4() :x(0), y(0), z(0), w(0)
		{

		}

		int4(int _x, int _y, int _z, int _w) :x(_x), y(_y), z(_z), w(_w)
		{

		}

		int4(const int4& i4) : x(i4.x), y(i4.y), z(i4.z), w(i4.w)
		{

		}

		int4& operator=(const int4& i4)
		{
			if(this != &i4)
			{
				x = i4.x;
				y = i4.y;
				z = i4.z;
				w = i4.w;
			}
			return *this;
		}

		int dot(const int4& rhs) const
		{
			return (x * rhs.x) + (y * rhs.y) + (z * rhs.z) + (w * rhs.w);
		}

		int4 operator*(const int& iVal) const
		{
			return int4(x*iVal, y*iVal, z*iVal, w*iVal);
		}

	};
	struct float2
	{
	public: 
		float x, y;


		float2() :x(0.0f), y(0.0f)
		{

		}

		float2(float _x, float _y) :x(_x), y(_y)
		{

		}

		float2(const float2& f2) : x(f2.x), y(f2.y)
		{

		}

		float2& operator=(const float2& f2)
		{
			if(this!=&f2)
			{
				x = f2.x;
				y = f2.y;
			}
			return *this;
		}
	};

	struct float4
	{
	public: 
		float x, y, z, w;

		float4() :x(0.0f), y(0.0f), z(0.0f), w(0.0f)
		{

		}

		float4(float _x, float _y, float _z, float _w) :x(_x), y(_y), z(_z), w(_w)
		{

		}

		float4(const float4& f4) : x(f4.x),	y(f4.y), z(f4.z), w(f4.w)
		{

		}

		float4 operator*(const float& fVal) const
		{
			return float4(x*fVal, y*fVal, z*fVal, w*fVal);
		}

		float dot(const float4& rhs) const
		{
			return (x * rhs.x) + (y * rhs.y) + (z * rhs.z) + (w * rhs.w);
		}

		float4& operator=(const float4& f4)
		{
			if(this!=&f4)
			{
				x = f4.x;
				y = f4.y;
				z = f4.z;
				w = f4.w;
			}
			return *this;
		}
	};

	struct float3
	{
	public: 
		float x, y, z;

		float3() : x(0.0f), y(0.0f), z(0.0f)
		{

		}

		float3(const float4& f4) : x(f4.x),y(f4.y), z(f4.z)
		{

		}

		float3(float _x, float _y, float _z) :x(_x), y(_y), z(_z)
		{

		}

		float3(const float3& f3) : x(f3.x), y(f3.y), z(f3.z)
		{

		}
		
		float3 operator+(const float3 &f3) const
		{
			return float3(x + f3.x, y + f3.y, z + f3.z);
		}

		float3 operator-(const float3& rhs) const
		{
			return float3(x - rhs.x, y - rhs.y, z - rhs.z);
		}

		float3& operator=(const float4& f4)
		{
			x = f4.x;
			y = f4.y; 
			z = f4.z;
			return *this;
		}

		float3& operator=(const float3& f3)
		{
			if(this!=&f3)
			{
				x = f3.x;
				y = f3.y;
				z = f3.z;
			}
			return *this;
		}

		float dot(const float3& rhs) const
		{
			return (x * rhs.x) + (y * rhs.y) + (z * rhs.z);
		}

		float length() const
		{
			return sqrt(x * x + y * y + z * z);
		}

		float3 operator/(const float& fVal) const
		{
			assert(fVal!=0.0f);
			return float3(x/fVal, y/fVal, z/fVal);
		}

		float3 operator*(const float& fVal) const
		{
			return float3(x*fVal, y*fVal, z*fVal);
		}

		float3& normalized()
		{
			float len = length();
			if(len > 1E-16f)
			{
				len = 1.0f / len;
				x = (x * len);
				y = (y * len);
				z = (z * len);
			}
			return *this;
		}

		float3 cross(const float3& lhs) const
		{
			return float3(lhs.y * z - lhs.z * y, lhs.z * x - lhs.x * z,	lhs.x * y - lhs.y * x);
		}
	};
	

   struct Matrix4f
    {
    public:
        float m_data[16];
		
		Matrix4f()
		{
			memset(m_data, 0, sizeof(float) * 16);
		}

		explicit Matrix4f(const float val)
		{
			for(int i = 0; i < 16; i++)
			{
				m_data[i] = val;
			}
		}

        explicit Matrix4f(const std::vector<float>& vals)
		{
			for(int i = 0; i < 16; i++) 
			{
				m_data[i] = vals[i];
			}
		}

        Matrix4f(float m00, float m01, float m02, float m03,
				float m10, float m11, float m12, float m13, 
				float m20, float m21, float m22, float m23, 
				float m30, float m31, float m32, float m33)
		{
			m_data[0] = m00;
			m_data[1] = m01;
			m_data[2] = m02;
			m_data[3] = m03;
			m_data[4] = m10;
			m_data[5] = m11;
			m_data[6] = m12;
			m_data[7] = m13;
			m_data[8] = m20;
			m_data[9] = m21;
			m_data[10] = m22;
			m_data[11] = m23;
			m_data[12] = m30;
			m_data[13] = m31;
			m_data[14] = m32;
			m_data[15] = m33;
		}

		explicit Matrix4f(const float(&vals)[16])
		{
			std::copy(vals, vals + 16, m_data);
		}

		virtual ~Matrix4f()
		{

		}

		Matrix4f Transpose() const
		{
			return Matrix4f(m_data[0], m_data[4], m_data[8], m_data[12],
				m_data[1], m_data[5], m_data[9], m_data[13],
				m_data[2], m_data[6], m_data[10], m_data[14],
				m_data[3], m_data[7], m_data[11], m_data[15]);
		}

		float Det() const
		{
			const float m00 = m_data[0],  m01 = m_data[1],  m02 = m_data[2],  m03 = m_data[3],
				m10 = m_data[4],  m11 = m_data[5],  m12 = m_data[6],  m13 = m_data[7],
				m20 = m_data[8],  m21 = m_data[9],  m22 = m_data[10], m23 = m_data[11],
				m30 = m_data[12], m31 = m_data[13], m32 = m_data[14], m33 = m_data[15];

			return (m03*m12*m21*m30 - m02*m13*m21*m30 - m03*m11*m22*m30 + m01*m13*m22*m30 +
				m02*m11*m23*m30 - m01*m12*m23*m30 - m03*m12*m20*m31 + m02*m13*m20*m31 +
				m03*m10*m22*m31 - m00*m13*m22*m31 - m02*m10*m23*m31 + m00*m12*m23*m31 +
				m03*m11*m20*m32 - m01*m13*m20*m32 - m03*m10*m21*m32 + m00*m13*m21*m32 +
				m01*m10*m23*m32 - m00*m11*m23*m32 - m02*m11*m20*m33 + m01*m12*m20*m33 +
				m02*m10*m21*m33 - m00*m12*m21*m33 - m01*m10*m22*m33 + m00*m11*m22*m33);
		}

		Matrix4f Inverse() const
		{
			float outData[16];

			const float v1[] = { m_data[4], m_data[5], m_data[6], m_data[7] };
			const float v2[] = { m_data[8], m_data[9], m_data[10], m_data[11] };
			const float v3[] = { m_data[12], m_data[13], m_data[14], m_data[15] };

			const float v00 = v1[2] * v2[3] * v3[1] - v1[3] * v2[2] * v3[1] + v1[3] * v2[1] * v3[2] - v1[1] * v2[3] * v3[2] - v1[2] * v2[1] * v3[3] + v1[1] * v2[2] * v3[3];
			const float v10 = v1[3] * v2[2] * v3[0] - v1[2] * v2[3] * v3[0] - v1[3] * v2[0] * v3[2] + v1[0] * v2[3] * v3[2] + v1[2] * v2[0] * v3[3] - v1[0] * v2[2] * v3[3];
			const float v20 = v1[1] * v2[3] * v3[0] - v1[3] * v2[1] * v3[0] + v1[3] * v2[0] * v3[1] - v1[0] * v2[3] * v3[1] - v1[1] * v2[0] * v3[3] + v1[0] * v2[1] * v3[3];
			const float v30 = v1[2] * v2[1] * v3[0] - v1[1] * v2[2] * v3[0] - v1[2] * v2[0] * v3[1] + v1[0] * v2[2] * v3[1] + v1[1] * v2[0] * v3[2] - v1[0] * v2[1] * v3[2];

			const float v0[] = { m_data[0], m_data[1], m_data[2], m_data[3] };

			const float vdet = v0[3] * v1[2] * v2[1] * v3[0] - v0[3] * v1[1] * v2[2] * v3[0] - v0[2] * v1[3] * v2[1] * v3[0] + v0[1] * v1[3] * v2[2] * v3[0] +
				v0[2] * v1[1] * v2[3] * v3[0] - v0[1] * v1[2] * v2[3] * v3[0] - v0[3] * v1[2] * v2[0] * v3[1] + v0[2] * v1[3] * v2[0] * v3[1] +
				v0[3] * v1[0] * v2[2] * v3[1] - v0[2] * v1[0] * v2[3] * v3[1] - v0[0] * v1[3] * v2[2] * v3[1] + v0[0] * v1[2] * v2[3] * v3[1] +
				v0[3] * v1[1] * v2[0] * v3[2] - v0[1] * v1[3] * v2[0] * v3[2] - v0[3] * v1[0] * v2[1] * v3[2] + v0[0] * v1[3] * v2[1] * v3[2] +
				v0[1] * v1[0] * v2[3] * v3[2] - v0[2] * v1[1] * v2[0] * v3[3] - v0[0] * v1[1] * v2[3] * v3[2] + v0[1] * v1[2] * v2[0] * v3[3] +
				v0[2] * v1[0] * v2[1] * v3[3] - v0[0] * v1[2] * v2[1] * v3[3] - v0[1] * v1[0] * v2[2] * v3[3] + v0[0] * v1[1] * v2[2] * v3[3];

			const float v01 = v0[3] * v2[2] * v3[1] - v0[2] * v2[3] * v3[1] - v0[3] * v2[1] * v3[2] + v0[1] * v2[3] * v3[2] + v0[2] * v2[1] * v3[3] - v0[1] * v2[2] * v3[3];
			const float v02 = v0[2] * v1[3] * v3[1] - v0[3] * v1[2] * v3[1] + v0[3] * v1[1] * v3[2] - v0[1] * v1[3] * v3[2] - v0[2] * v1[1] * v3[3] + v0[1] * v1[2] * v3[3];
			const float v03 = v0[3] * v1[2] * v2[1] - v0[2] * v1[3] * v2[1] - v0[3] * v1[1] * v2[2] + v0[1] * v1[3] * v2[2] + v0[2] * v1[1] * v2[3] - v0[1] * v1[2] * v2[3];

			float invdet = 0;
			if (fabs(vdet) > 0.0000001f)
			{ 
				invdet = 1.0f / vdet; 
			}

			outData[0] = v00 * invdet;
			outData[1] = v01 * invdet;
			outData[2] = v02 * invdet;
			outData[3] = v03 * invdet;

			const float v11 = v0[2] * v2[3] * v3[0] - v0[3] * v2[2] * v3[0] + v0[3] * v2[0] * v3[2] - v0[0] * v2[3] * v3[2] - v0[2] * v2[0] * v3[3] + v0[0] * v2[2] * v3[3];
			const float v12 = v0[3] * v1[2] * v3[0] - v0[2] * v1[3] * v3[0] - v0[3] * v1[0] * v3[2] + v0[0] * v1[3] * v3[2] + v0[2] * v1[0] * v3[3] - v0[0] * v1[2] * v3[3];
			const float v13 = v0[2] * v1[3] * v2[0] - v0[3] * v1[2] * v2[0] + v0[3] * v1[0] * v2[2] - v0[0] * v1[3] * v2[2] - v0[2] * v1[0] * v2[3] + v0[0] * v1[2] * v2[3];

			outData[4] = v10 * invdet;
			outData[5] = v11 * invdet;
			outData[6] = v12 * invdet;
			outData[7] = v13 * invdet;

			const float v21 = v0[3] * v2[1] * v3[0] - v0[1] * v2[3] * v3[0] - v0[3] * v2[0] * v3[1] + v0[0] * v2[3] * v3[1] + v0[1] * v2[0] * v3[3] - v0[0] * v2[1] * v3[3];
			const float v22 = v0[1] * v1[3] * v3[0] - v0[3] * v1[1] * v3[0] + v0[3] * v1[0] * v3[1] - v0[0] * v1[3] * v3[1] - v0[1] * v1[0] * v3[3] + v0[0] * v1[1] * v3[3];
			const float v23 = v0[3] * v1[1] * v2[0] - v0[1] * v1[3] * v2[0] - v0[3] * v1[0] * v2[1] + v0[0] * v1[3] * v2[1] + v0[1] * v1[0] * v2[3] - v0[0] * v1[1] * v2[3];

			outData[8] = v20 * invdet;
			outData[9] = v21 * invdet;
			outData[10] = v22 * invdet;
			outData[11] = v23 * invdet;

			const float v31 = v0[1] * v2[2] * v3[0] - v0[2] * v2[1] * v3[0] + v0[2] * v2[0] * v3[1] - v0[0] * v2[2] * v3[1] - v0[1] * v2[0] * v3[2] + v0[0] * v2[1] * v3[2];
			const float v32 = v0[2] * v1[1] * v3[0] - v0[1] * v1[2] * v3[0] - v0[2] * v1[0] * v3[1] + v0[0] * v1[2] * v3[1] + v0[1] * v1[0] * v3[2] - v0[0] * v1[1] * v3[2];
			const float v33 = v0[1] * v1[2] * v2[0] - v0[2] * v1[1] * v2[0] + v0[2] * v1[0] * v2[1] - v0[0] * v1[2] * v2[1] - v0[1] * v1[0] * v2[2] + v0[0] * v1[1] * v2[2];

			outData[12] = v30 * invdet;
			outData[13] = v31 * invdet;
			outData[14] = v32 * invdet;
			outData[15] = v33 * invdet;

			return Matrix4f(outData);
		}
		
		void SetMatrix(const float (&pose)[6]);

		static float4 Transform(const Matrix4f& mat, const float4& vec)
		{
			return float4(
				vec.x * mat.m_data[0] + vec.y * mat.m_data[4] + vec.z * mat.m_data[8] + vec.w * mat.m_data[12],
				vec.x * mat.m_data[1] + vec.y * mat.m_data[5] + vec.z * mat.m_data[9] + vec.w * mat.m_data[13],
				vec.x * mat.m_data[2] + vec.y * mat.m_data[6] + vec.z * mat.m_data[10] + vec.w * mat.m_data[14],
				vec.x * mat.m_data[3] + vec.y * mat.m_data[7] + vec.z * mat.m_data[11] + vec.w * mat.m_data[15]);
		}

		static float3 Transform(const Matrix4f& M, const float3& vec)
		{
			float4 v4(vec.x, vec.y, vec.z, 1);
			float4 vresult = Matrix4f::Transform(M, v4);
			return float3(vresult.x, vresult.y, vresult.z);
		}

		void Set(const std::vector<float>& M)
		{
			if (M.size() == 16)
			{
				std::copy(&M[0], &M[0] + 16, m_data);
			}
		}

        void Get(std::vector<float>& M) const
		{
			M = std::vector<float>(m_data, m_data + 16);
		}

        float At(int i, int j) const
		{
			return m_data[j + i * 4];
		}

        void Set(int i, int j, float newValue)
		{
			m_data[j + i * 4] = newValue;
		}

        Matrix4f MultiplyWith(const Matrix4f& M) const
		{
			Matrix4f result;

			const float a00 = m_data[0],	a01 = m_data[1],	a02 = m_data[2],	a03 = m_data[3],
				a10 = m_data[4],	a11 = m_data[5],	a12 = m_data[6],	a13 = m_data[7],
				a20 = m_data[8],	a21 = m_data[9],	a22 = m_data[10],	a23 = m_data[11],
				a30 = m_data[12],	a31 = m_data[13],	a32 = m_data[14],	a33 = m_data[15],
				b00 = M.m_data[0],	b01 = M.m_data[1],	b02 = M.m_data[2],	b03 = M.m_data[3],
				b10 = M.m_data[4],	b11 = M.m_data[5],	b12 = M.m_data[6],	b13 = M.m_data[7],
				b20 = M.m_data[8],	b21 = M.m_data[9],	b22 = M.m_data[10], b23 = M.m_data[11],
				b30 = M.m_data[12],	b31 = M.m_data[13], b32 = M.m_data[14], b33 = M.m_data[15];

			float *r = result.m_data;
			*r++ = (a00 * b00) + (a01 * b10) + (a02 * b20) + (a03 * b30);
			*r++ = (a00 * b01) + (a01 * b11) + (a02 * b21) + (a03 * b31);
			*r++ = (a00 * b02) + (a01 * b12) + (a02 * b22) + (a03 * b32);
			*r++ = (a00 * b03) + (a01 * b13) + (a02 * b23) + (a03 * b33);

			*r++ = (a10 * b00) + (a11 * b10) + (a12 * b20) + (a13 * b30);
			*r++ = (a10 * b01) + (a11 * b11) + (a12 * b21) + (a13 * b31);
			*r++ = (a10 * b02) + (a11 * b12) + (a12 * b22) + (a13 * b32);
			*r++ = (a10 * b03) + (a11 * b13) + (a12 * b23) + (a13 * b33);

			*r++ = (a20 * b00) + (a21 * b10) + (a22 * b20) + (a23 * b30);
			*r++ = (a20 * b01) + (a21 * b11) + (a22 * b21) + (a23 * b31);
			*r++ = (a20 * b02) + (a21 * b12) + (a22 * b22) + (a23 * b32);
			*r++ = (a20 * b03) + (a21 * b13) + (a22 * b23) + (a23 * b33);

			*r++ = (a30 * b00) + (a31 * b10) + (a32 * b20) + (a33 * b30);
			*r++ = (a30 * b01) + (a31 * b11) + (a32 * b21) + (a33 * b31);
			*r++ = (a30 * b02) + (a31 * b12) + (a32 * b22) + (a33 * b32);
			*r   = (a30 * b03) + (a31 * b13) + (a32 * b23) + (a33 * b33);

			return result;
		}
        Matrix4f operator-(const Matrix4f& M) const
		{
			Matrix4f Result;
			for (int i = 0; i < 16; i++)
			{
				Result.m_data[i] = m_data[i] - M.m_data[i];
			}
			return Result;
		}
        Matrix4f operator+(const Matrix4f& M) const
		{
			Matrix4f Result;
			for (int i = 0; i < 16; i++)
			{
				Result.m_data[i] = m_data[i] + M.m_data[i];
			}
			return Result;
		}
        Matrix4f operator*(const float val) const
		{
			return Matrix4f(m_data[0]  * val,  m_data[1]  * val, m_data[2]  * val, m_data[3]  * val,
				m_data[4]  * val,  m_data[5]  * val, m_data[6]  * val, m_data[7]  * val,
				m_data[8]  * val,  m_data[9]  * val, m_data[10] * val, m_data[11] * val,
				m_data[12] * val,  m_data[13] * val, m_data[14] * val, m_data[15] * val);
		}
		
		Matrix4f operator-(const float val) const
		{
			Matrix4f Result;
			for (int i = 0; i < 16; i++)
			{
				Result.m_data[i] = m_data[i] - val;
			}
			return Result;
		}
        Matrix4f operator+(const float val) const
		{
			Matrix4f Result;
			for(int i = 0; i < 16; i++)
			{
				Result.m_data[i] = m_data[i] + val;
			}
			return Result;
		}
		
		Matrix4f operator/(const float val) const
		{
			Matrix4f Result;
			for (int i = 0; i < 16; i++)
			{
				Result.m_data[i] = m_data[i] / val;
			}
			return Result;
		}

        Matrix4f operator*(const Matrix4f& M) const
		{
			return MultiplyWith(M);
		}

		float4 operator*(const float4& v) const
		{
			return float4((*(m_data)* v.x) + (*(m_data + 1)  * v.y) + (*(m_data + 2)  * v.z) + (*(m_data + 3)  * v.w),
				(*(m_data + 4) * v.x) + (*(m_data + 5)  * v.y) + (*(m_data + 6)  * v.z) + (*(m_data + 7)  * v.w),
				(*(m_data + 8) * v.x) + (*(m_data + 9)  * v.y) + (*(m_data + 10) * v.z) + (*(m_data + 11) * v.w),
				(*(m_data + 12) * v.x) + (*(m_data + 13) * v.y) + (*(m_data + 14) * v.z) + (*(m_data + 15) * v.w));
		}
		
		bool operator==(const Matrix4f &matrix) const
		{
			for(int i = 0; i < 16; ++i)
			{
				if (m_data[i] != matrix.m_data[i])
				{
					return false;
				}
			}
			return true;
		}

		Matrix4f& operator=(const Matrix4f& M)
		{
			if (this != &M)
			{
				std::copy(M.m_data, M.m_data + 16, m_data);
			}
			return *this;
		}
	
		template<typename charT, typename traits>
		friend std::basic_ostream<charT, traits> &
			operator<< (std::basic_ostream<charT, traits> &lhs, Matrix4f const &rhs)
		{
			lhs << "\n[" << rhs.m_data[0] << " " << rhs.m_data[1] << " " << rhs.m_data[2] << " " << rhs.m_data[3] << "]\n"
				<< "[" << rhs.m_data[4] << " " << rhs.m_data[5] << " " << rhs.m_data[6] << " " << rhs.m_data[7] << "]\n"
				<< "[" << rhs.m_data[8] << " " << rhs.m_data[9] << " " << rhs.m_data[10] << " " << rhs.m_data[11] << "]\n"
				<< "[" << rhs.m_data[12] << " " << rhs.m_data[13] << " " << rhs.m_data[14] << " " << rhs.m_data[15] << "]\n";
			return lhs;
		}

        friend float4 operator*(const float4 &v, const Matrix4f &matrix)
        {
            return float4(v.dot(float4(matrix.m_data[0], matrix.m_data[4], matrix.m_data[8], matrix.m_data[12])),
						  v.dot(float4(matrix.m_data[1], matrix.m_data[5], matrix.m_data[9], matrix.m_data[13])),
						  v.dot(float4(matrix.m_data[2], matrix.m_data[6], matrix.m_data[10], matrix.m_data[14])),
						  v.dot(float4(matrix.m_data[3], matrix.m_data[7], matrix.m_data[11], matrix.m_data[15])));
        }
    };
	
	class PoseMatrix4f : public Matrix4f
	{
		public:
			PoseMatrix4f() : Matrix4f(
				1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 1.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f)
			{

			}

			PoseMatrix4f(const PoseMatrix4f &poseMat) : Matrix4f(poseMat.m_data[0], poseMat.m_data[1], poseMat.m_data[2], poseMat.m_data[3],
				poseMat.m_data[4], poseMat.m_data[5], poseMat.m_data[6], poseMat.m_data[7],
				poseMat.m_data[8], poseMat.m_data[9], poseMat.m_data[10], poseMat.m_data[11],
				0.0f, 0.0f, 0.0f, 1.0f)
			{

			}
			
			PoseMatrix4f(const std::vector<float>& vals) : Matrix4f(vals[0], vals[1], vals[2], vals[3],
				vals[4], vals[5], vals[6], vals[7],
				vals[8], vals[9], vals[10], vals[11],
				0.0f, 0.0f, 0.0f, 1.0f)
			{
				assert(vals.size() >= 12);
			}
			
			PoseMatrix4f(float m00, float m01, float m02, float m03,
						 float m10, float m11, float m12, float m13,
						 float m20, float m21, float m22, float m23) :
						 Matrix4f(m00, m01, m02, m03,
						    	  m10, m11, m12, m13,
								  m20, m21, m22, m23,
								  0.0f, 0.0f, 0.0f, 1.0f)
			{

			}

			PoseMatrix4f(const float(&rotationMat)[9], const float3 & translationVector) :
						 Matrix4f(rotationMat[0], rotationMat[1], rotationMat[2], translationVector.x,
						 rotationMat[3], rotationMat[4], rotationMat[5], translationVector.y,
						 rotationMat[6], rotationMat[7], rotationMat[8], translationVector.z,
						 0.0f, 0.0f, 0.0f, 1.0f)
			{


			}

			PoseMatrix4f(const float3& rotationMatRow1, const float3& rotationMatRow2, const float3& rotationMatRow3, const float3& translationVector) :
				Matrix4f(rotationMatRow1.x, rotationMatRow1.y, rotationMatRow1.z, translationVector.x,
				rotationMatRow2.x, rotationMatRow2.y, rotationMatRow2.z, translationVector.y,
				rotationMatRow3.x, rotationMatRow3.y, rotationMatRow3.z, translationVector.z,
				0.0f, 0.0f, 0.0f, 1.0f)
			{

			}

			PoseMatrix4f(const float(&vals)[12])
			{
				std::copy(vals, vals + 12, m_data);
				m_data[12] = 0;
				m_data[13] = 0;
				m_data[14] = 0;
				m_data[15] = 1;
			}

			PoseMatrix4f(const float(&vals)[16]);

			PoseMatrix4f(const float(&pose)[6])
			{
				m_data[3] = pose[0];
				m_data[7] = pose[1];
				m_data[11] = pose[2];

				float w[3];
				w[0] = pose[3];
				w[1] = pose[4];
				w[2] = pose[5];
				const float thetaSquare = w[0] * w[0] + w[1] * w[1] + w[2] * w[2];
				const float theta = sqrt(thetaSquare);

				if (theta < 1e-6)
				{
					m_data[0] = 1;
					m_data[1] = 0;
					m_data[2] = 0;

					m_data[4] = 0;
					m_data[5] = 1;
					m_data[6] = 0;

					m_data[8] = 0;
					m_data[9] = 0;
					m_data[10] = 1;
				}
				else
				{
					const float invTheta = 1.0f / theta;

					const float ct = cos(theta);
					const float st = sin(theta);

					const float ux = invTheta * w[0];
					const float uy = invTheta * w[1];
					const float uz = invTheta * w[2];

					m_data[0] = (ct - 1)*uy*uy + (ct - 1)*uz*uz + 1;
					m_data[1] = -st*uz - ux*uy*(ct - 1);
					m_data[2] = st*uy - ux*uz*(ct - 1);

					m_data[4] = st*uz - ux*uy*(ct - 1);
					m_data[5] = (ct - 1)*ux*ux + (ct - 1)*uz*uz + 1;
					m_data[6] = -st*ux - uy*uz*(ct - 1);

					m_data[8] = -st*uy - ux*uz*(ct - 1);
					m_data[9] = st*ux - uy*uz*(ct - 1);
					m_data[10] = (ct - 1)*ux*ux + (ct - 1)*uy*uy + 1;
				}

				m_data[12] = 0;
				m_data[13] = 0;
				m_data[14] = 0;
				m_data[15] = 1;
			}
			
			
			PoseMatrix4f(const Matrix4f& poseMat)
			{
				std::copy(poseMat.m_data, poseMat.m_data + 12, m_data);

				m_data[12] = 0.0f;
				m_data[13] = 0.0f;
				m_data[14] = 0.0f;
				m_data[15] = 1.0f;
			}

			PoseMatrix4f& operator=(const Matrix4f& poseMat)
			{
				if (this != &poseMat)
				{
					std::copy(poseMat.m_data, poseMat.m_data + 12, m_data);

					m_data[12] = 0.0f;
					m_data[13] = 0.0f;
					m_data[14] = 0.0f;
					m_data[15] = 1.0f;
				}
				return *this;
			}

			bool operator==(const PoseMatrix4f &pose) const;
			PoseMatrix4f& operator=(const PoseMatrix4f& poseMat)
			{
				if(this != &poseMat)
				{
					memcpy_s(m_data, sizeof(m_data), poseMat.m_data, sizeof(poseMat.m_data));
				}
				return *this;
			}
			~PoseMatrix4f()
			{

			}
			
			PoseMatrix4f Inverse() const
			{
				return PoseMatrix4f(m_data[0], m_data[4], m_data[8], -((m_data[0] * m_data[3]) + (m_data[4] * m_data[7]) + (m_data[8] * m_data[11])),
					m_data[1], m_data[5], m_data[9], -((m_data[1] * m_data[3]) + (m_data[5] * m_data[7]) + (m_data[9] * m_data[11])),
					m_data[2], m_data[6], m_data[10], -((m_data[2] * m_data[3]) + (m_data[6] * m_data[7]) + (m_data[10] * m_data[11])));
			}

			PoseMatrix4f operator*(const PoseMatrix4f& poseMat2) const
			{
				const float
					a00 = m_data[0], a01 = m_data[1], a02 = m_data[2], a03 = m_data[3],
					a10 = m_data[4], a11 = m_data[5], a12 = m_data[6], a13 = m_data[7],
					a20 = m_data[8], a21 = m_data[9], a22 = m_data[10], a23 = m_data[11],

					b00 = poseMat2.m_data[0], b01 = poseMat2.m_data[1], b02 = poseMat2.m_data[2], b03 = poseMat2.m_data[3],
					b10 = poseMat2.m_data[4], b11 = poseMat2.m_data[5], b12 = poseMat2.m_data[6], b13 = poseMat2.m_data[7],
					b20 = poseMat2.m_data[8], b21 = poseMat2.m_data[9], b22 = poseMat2.m_data[10], b23 = poseMat2.m_data[11];


				return PoseMatrix4f(
					(a00 * b00) + (a01 * b10) + (a02 * b20),
					(a00 * b01) + (a01 * b11) + (a02 * b21),
					(a00 * b02) + (a01 * b12) + (a02 * b22),
					(a00 * b03) + (a01 * b13) + (a02 * b23) + a03,

					(a10 * b00) + (a11 * b10) + (a12 * b20),
					(a10 * b01) + (a11 * b11) + (a12 * b21),
					(a10 * b02) + (a11 * b12) + (a12 * b22),
					(a10 * b03) + (a11 * b13) + (a12 * b23) + a13,

					(a20 * b00) + (a21 * b10) + (a22 * b20),
					(a20 * b01) + (a21 * b11) + (a22 * b21),
					(a20 * b02) + (a21 * b12) + (a22 * b22),
					(a20 * b03) + (a21 * b13) + (a22 * b23) + a23);
			}

			float4 operator*(const float4& v) const
			{
				return float4((*(m_data)     * v.x) + (*(m_data + 1)  * v.y) + (*(m_data + 2)  * v.z) + (*(m_data + 3)  * v.w),
					(*(m_data + 4) * v.x) + (*(m_data + 5)  * v.y) + (*(m_data + 6)  * v.z) + (*(m_data + 7)  * v.w),
					(*(m_data + 8) * v.x) + (*(m_data + 9)  * v.y) + (*(m_data + 10) * v.z) + (*(m_data + 11) * v.w),
					v.w);
			}

			static bool validatePoseMatrix(const float(&poseMatrix)[12])
			{
				return PoseMatrix4f::validatePoseMatrix(PoseMatrix4f(poseMatrix));
			}

			static bool validatePoseMatrix(const PoseMatrix4f& pose)
			{
				const float *poseMatrix = &pose.m_data[0];

				float3 row1(poseMatrix[0], poseMatrix[1], poseMatrix[2]);
				float3 row2(poseMatrix[4], poseMatrix[5], poseMatrix[6]);
				float3 row3(poseMatrix[8], poseMatrix[9], poseMatrix[10]);
				float3 col1(poseMatrix[0], poseMatrix[4], poseMatrix[8]);
				float3 col2(poseMatrix[1], poseMatrix[5], poseMatrix[9]);
				float3 col3(poseMatrix[2], poseMatrix[6], poseMatrix[10]);

				// matrix is orthogonal : Tranpose(A) * A = I
				bool isValid = true;
				isValid = isValid && (abs(col1.dot(col1) - 1) < PRECISION);
				isValid = isValid && (abs(col1.dot(col2)) < PRECISION);
				isValid = isValid && (abs(col1.dot(col3)) < PRECISION);
				isValid = isValid && (abs(col2.dot(col2) - 1) < PRECISION);
				isValid = isValid && (abs(col2.dot(col3)) < PRECISION);
				isValid = isValid && (abs(col3.dot(col3) - 1) < PRECISION);

				float determinant =
					poseMatrix[0] * (poseMatrix[5] * poseMatrix[10] - poseMatrix[6] * poseMatrix[9]) -
					poseMatrix[1] * (poseMatrix[4] * poseMatrix[10] - poseMatrix[6] * poseMatrix[8]) +
					poseMatrix[2] * (poseMatrix[4] * poseMatrix[9] - poseMatrix[5] * poseMatrix[8]);

				//determinant is 1
				isValid = isValid && (abs(determinant - 1) < PRECISION);

				//last row is 0, 0, 0, 1
				isValid = isValid && (std::abs(poseMatrix[12]) < PRECISION && std::abs(poseMatrix[13]) < PRECISION && 
					std::abs(poseMatrix[14]) < PRECISION && std::abs(poseMatrix[15] - 1.0f) < PRECISION);
				return isValid;
			}

			template<typename charT, typename traits>
			friend std::basic_ostream<charT, traits> &
				operator<< (std::basic_ostream<charT, traits> &lhs, PoseMatrix4f const &rhs)
			{
				lhs << "\n[" << rhs.m_data[0] << " " << rhs.m_data[1] << " " << rhs.m_data[2] << " " << rhs.m_data[3] << "]\n"
					<< "[" << rhs.m_data[4] << " " << rhs.m_data[5] << " " << rhs.m_data[6] << " " << rhs.m_data[7] << "]\n"
					<< "[" << rhs.m_data[8] << " " << rhs.m_data[9] << " " << rhs.m_data[10] << " " << rhs.m_data[11] << "]\n"
					<< "[" << rhs.m_data[12] << " " << rhs.m_data[13] << " " << rhs.m_data[14] << " " << rhs.m_data[15] << "]\n";
				return lhs;
			}
	};
	
	inline void crossProduct(float *result, const float *lhs, const float *rhs)
	{
		result[0] = lhs[1] * rhs[2] - lhs[2] * rhs[1];
		result[1] = lhs[2] * rhs[0] - lhs[0] * rhs[2];
		result[2] = lhs[0] * rhs[1] - lhs[1] * rhs[0];
	}
}