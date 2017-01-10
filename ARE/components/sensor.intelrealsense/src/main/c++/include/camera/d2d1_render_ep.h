/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2010-2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/
#pragma once
#include "d2d1_render.h"
#include <vector>

class D2D1RenderEP : public D2D1Render {
public:

	D2D1RenderEP() :D2D1Render(), red(), green(), blue(), panel(), leftClicks(), rightClicks(), rectangle(), displayStartPoint(true) {
		ResetPointers();
	}

	virtual ~D2D1RenderEP() {
		if (red) red->Release();
		if(blue) blue->Release();
		if(green) green->Release();
	}

	// pointer manupulation
	void ResetPointers() {
		ResetStartPointer();
		ResetEndPointer();
		ResetRectangle();
		ResetLeftRightClicks();
		ResetOrigBitmapSize();
		mouse.x = mouse.y = -1;
	}

	void ResetStartPointer() {
		start.x = start.y = -1;
	}

	void ResetEndPointer() {
		end.x = end.y = -1;
	}

	void ResetLeftRightClicks(){
		if(leftClicks.size() > 0) leftClicks.clear();
		if(rightClicks.size() > 0) rightClicks.clear();
	}

	void ResetRectangle(){
		rectangle.top = rectangle.bottom = rectangle.right = rectangle.left = -1;
	}

	bool IsStartPointerSet() {
		return start.x >= 0 && start.y >= 0;
	}

	bool IsEndPointerSet() {
		return end.x >= 0 && end.y >= 0;
	}

	bool IsPointValid(D2D1_POINT_2F point) {
		return (point.x >= box.left && point.x < box.right && point.y >= box.top && point.y < box.bottom);
	}

	void SetStartPointer(D2D1_POINT_2F point) {
		start = point;
	}

	void SetEndPointer(D2D1_POINT_2F point) {
		end = point;
	}

	void SetRectangle(D2D1_POINT_2F pos){
		if(rectangle.left == -1 && rectangle.top == -1){
			rectangle.right = rectangle.left = pos.x;
			rectangle.bottom = rectangle.top = pos.y;
		}else{
			rectangle.right = pos.x;
			rectangle.bottom = pos.y;
		}

	}

	void SetShowStartPoint(bool showPoint){
		displayStartPoint = showPoint;
	}

	void InsertClick(D2D1_POINT_2F pos, bool isLeft){
		if(isLeft) leftClicks.push_back(pos);
		else rightClicks.push_back(pos);
	}

	/* Set the current mouse pointer */
	void SetMousePointer(D2D1_POINT_2F point) {
		mouse = point;
	}

	/* Map the mouse position to the display panel position */
	bool GetClickPosition(LPARAM lParam, D2D1_POINT_2F &pos) {
		/* Get the windows rect and map to client rect */
		RECT rect = {};
		GetWindowRect(panel, &rect);
		ScreenToClient(GetParent(panel), (LPPOINT)&rect);

		/* Map mouse position relative to the client rect */
		pos.x = (float)GET_X_LPARAM(lParam)-(float)rect.left;
		pos.y = (float)GET_Y_LPARAM(lParam)-(float)rect.top;
		if (IsPointValid(pos)) return true;
		pos.x = pos.y = -1;
		return false;
	}

	PXCPointI32 GetScaledPoint(float x, float y){
		D2D1_SIZE_U size = (bitmap)? bitmap->GetPixelSize(): origBitmapSize;
		D2D1_RECT_F box = this->box;

		PXCPointI32 point;
		point.x = (x - box.left) / (box.right - box.left)*(float)size.width;
		point.y = (y - box.top) / (box.bottom - box.top)*(float)size.height;
		return point;
	}

	D2D1_POINT_2F GetScaledStartPointer() {
		D2D1_SIZE_U size = (bitmap)? bitmap->GetPixelSize(): origBitmapSize;
		D2D1_RECT_F box = this->box;

		D2D1_POINT_2F start1;
		start1.x = (start.x - box.left) / (box.right - box.left)*(float)size.width;
		start1.y = (start.y - box.top) / (box.bottom - box.top)*(float)size.height;
		return start1;
	}

	D2D1_POINT_2F GetScaledEndPointer() {
		D2D1_SIZE_U size = (bitmap)? bitmap->GetPixelSize(): origBitmapSize;
		D2D1_RECT_F box = this->box;

		D2D1_POINT_2F end1;
		end1.x = (end.x - box.left) / (box.right - box.left)*(float)size.width;
		end1.y = (end.y - box.top) / (box.bottom - box.top)*(float)size.height;
		return end1;
	}

	void SetHWND(HWND panel) {
		this->panel = panel;
		D2D1Render::SetHWND(panel);

		if (!context2) return;
		if (red) red->Release(), red = 0;
		context2->CreateSolidColorBrush(D2D1::ColorF(D2D1::ColorF::OrangeRed), &red);
		if (green) green->Release(), green = 0;
		context2->CreateSolidColorBrush(D2D1::ColorF(D2D1::ColorF::GreenYellow), &green);
		if (blue) blue->Release(), blue = 0;
		context2->CreateSolidColorBrush(D2D1::ColorF(D2D1::ColorF::Blue), &blue);
	}

protected:

	D2D1_POINT_2F start, end, mouse;
	D2D1_RECT_F rectangle;
	bool displayStartPoint;
	ID2D1SolidColorBrush* red;
	ID2D1SolidColorBrush* green;
	ID2D1SolidColorBrush* blue;
	HWND panel;
	D2D1_SIZE_U origBitmapSize;
	std::vector<D2D1_POINT_2F> leftClicks, rightClicks;

	void ResetOrigBitmapSize()
	{
		origBitmapSize.width = origBitmapSize.height = -1;
	}

	virtual void UpdatePanelEx(ID2D1Bitmap *bitmap) {
		D2D1Render::UpdatePanelEx(bitmap);

		D2D1_POINT_2F dpi = { 96, 96 };
		context2->GetDpi(&dpi.x, &dpi.y);
		dpi.x /= 96;
		dpi.y /= 96;

		if(origBitmapSize.width != bitmap->GetPixelSize().width && origBitmapSize.height != bitmap->GetPixelSize().height)
			origBitmapSize = bitmap->GetPixelSize();

		D2D1_POINT_2F start2 = { start.x / dpi.x, start.y / dpi.y };
		if (start2.x >= 0 && start2.y >= 0 && red) {
			const int cross_size = 5;

			if(displayStartPoint){
				/* Draw a cross at the starting point of user selection */
				context2->DrawLine(D2D1::Point2F(start2.x - cross_size, start2.y - cross_size), D2D1::Point2F(start2.x + cross_size, start2.y + cross_size), green);
				context2->DrawLine(D2D1::Point2F(start2.x - cross_size, start2.y + cross_size), D2D1::Point2F(start2.x + cross_size, start2.y - cross_size), green);
			}

			D2D1_POINT_2F end2 = (end.x >= 0 && end.y >= 0) ? end : mouse;
			end2.x /= dpi.x;
			end2.y /= dpi.y;
			if (end2.x >= 0 && end2.y >= 0) {
				/* Draw a line of user selection */
				context2->DrawLine(D2D1::Point2F(start2.x, start2.y), D2D1::Point2F(end2.x, end2.y), red);

				/* Draw a cross at the end point of user selection */
				context2->DrawLine(D2D1::Point2F(end2.x - cross_size, end2.y - cross_size), D2D1::Point2F(end2.x + cross_size, end2.y + cross_size), green);
				context2->DrawLine(D2D1::Point2F(end2.x - cross_size, end2.y + cross_size), D2D1::Point2F(end2.x + cross_size, end2.y - cross_size), green);
			}
		}

		/* Draw red line passing through set of points */
		bool skipFirst = false;
		if(leftClicks.size() > 0){
			skipFirst = true;
			for (std::vector<D2D1_POINT_2F>::iterator it = leftClicks.begin() ; it != leftClicks.end(); ++it){
				if(skipFirst) { skipFirst = false; continue; }
				D2D1_POINT_2F curPoint; curPoint.x = it->x/dpi.x; curPoint.y = it->y/dpi.y;
				D2D1_POINT_2F prevPoint = *std::prev(it);
				D2D1_POINT_2F prevPoint2; prevPoint2.x = prevPoint.x/dpi.x;  prevPoint2.y = prevPoint.y/dpi.y;
				context2->DrawLine(curPoint, prevPoint2, blue);
			}
		}

		/* Draw green line passing through set of points */
		if(rightClicks.size() > 0){
			skipFirst = true;
			for (std::vector<D2D1_POINT_2F>::iterator it = rightClicks.begin() ; it != rightClicks.end(); ++it){
				if(skipFirst) { skipFirst = false; continue; }
				D2D1_POINT_2F curPoint; curPoint.x = it->x/dpi.x; curPoint.y = it->y/dpi.y;
				D2D1_POINT_2F prevPoint = *std::prev(it);
				D2D1_POINT_2F prevPoint2; prevPoint2.x = prevPoint.x/dpi.x;  prevPoint2.y = prevPoint.y/dpi.y;
				context2->DrawLine(curPoint, prevPoint2, green);
			}
		}

		/* Draw a red rectangle with top left and bottom right points */
		if(rectangle.top != rectangle.bottom){
			D2D1_RECT_F rectangle2;
			rectangle2.left = rectangle.left/dpi.x;
			rectangle2.right = rectangle.right/dpi.x;
			rectangle2.top = rectangle.top/dpi.y;
			rectangle2.bottom = rectangle.bottom/dpi.y;
			context2->DrawRectangle(rectangle2, red, 1.0);
		}
	}
};
