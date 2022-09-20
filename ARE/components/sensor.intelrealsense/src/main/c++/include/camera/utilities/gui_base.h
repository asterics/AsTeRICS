/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2010-2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/
#pragma once
#include <Windows.h>
#include <windowsx.h>

class GUIBase {

	/* Protect objects of the class from copying */
	GUIBase(GUIBase&) {}

	GUIBase& operator=(GUIBase&) {
		return *this;
	}

public:

	GUIBase(const int controls1[], int ncontrols1):controls(controls1), ncontrols(ncontrols1), hwndDlg(0), status(0), panel(0), file() {
		layout = new RECT[ncontrols1 + 3];
	}

	virtual ~GUIBase() {
		if (layout) delete [] layout;
	}

	void OnInitDialog(HWND hwndDlg, HWND panel, HWND status) {
		this->hwndDlg = hwndDlg;
		this->panel = panel;
		this->status = status;
		menu = GetMenu(hwndDlg);

		// save the GUI layout
		if (layout) {
			GetClientRect(hwndDlg, &layout[0]);
			ClientToScreen(hwndDlg, (LPPOINT)&layout[0].left);
			ClientToScreen(hwndDlg, (LPPOINT)&layout[0].right);
			GetWindowRect(panel, &layout[1]);
			GetWindowRect(status, &layout[2]);
			for (int i = 0; i < ncontrols; i++)
				GetWindowRect(GetDlgItem(hwndDlg, controls[i]), &layout[3 + i]);
		}
	}

	void OnCancel() {
		if (hwndDlg) DestroyWindow(hwndDlg);
		PostQuitMessage(0);
	}

	void OnStart(int stop_control) {
		Button_Enable(GetDlgItem(hwndDlg, stop_control), TRUE);

		for (int i = 0; i < GetMenuItemCount(menu); i++)
			::EnableMenuItem(menu, i, MF_BYPOSITION | MF_GRAYED);
		DrawMenuBar(hwndDlg);
	}

	void OnStart(int start_control, int stop_control) {
		Button_Enable(GetDlgItem(hwndDlg, start_control), FALSE);
		OnStart(stop_control);
	}

	void OnStop(int start_control, int stop_control) {
		Button_Enable(GetDlgItem(hwndDlg, start_control), TRUE);
		Button_Enable(GetDlgItem(hwndDlg, stop_control), FALSE);

		for (int i = 0; i < GetMenuItemCount(menu); i++)
			::EnableMenuItem(menu, i, MF_BYPOSITION | MF_ENABLED);
		DrawMenuBar(hwndDlg);
	}

	void OnResize() {
		if (!layout) return;

		RECT rect;
		GetClientRect(hwndDlg, &rect);

		/* Status */
		SetWindowPos(status, hwndDlg, 0, rect.bottom - (layout[2].bottom - layout[2].top),
			rect.right - rect.left, (layout[2].bottom - layout[2].top), SWP_NOZORDER);

		/* Panel */
		SetWindowPos(panel, hwndDlg,
			(layout[1].left - layout[0].left), (layout[1].top - layout[0].top),
			rect.right - (layout[1].left - layout[0].left) - (layout[0].right - layout[1].right),
			rect.bottom - (layout[1].top - layout[0].top) - (layout[0].bottom - layout[1].bottom),
			SWP_NOZORDER);

		/* Buttons & CheckBoxes */
		for (int i = 0; i< ncontrols; i++) {
			SetWindowPos(GetDlgItem(hwndDlg, controls[i]), hwndDlg,
				rect.right - (layout[0].right - layout[3 + i].left), (layout[3 + i].top - layout[0].top),
				(layout[3 + i].right - layout[3 + i].left), (layout[3 + i].bottom - layout[3 + i].top),
				SWP_NOZORDER);
		}
	}

	void UpdateStatus(WCHAR *line) {
		SetWindowText(status, line);
	}

	int GetCheckedMenuIndex(int control) {
		HMENU menu1 = GetSubMenu(menu, control);
		for (int i = 0; i<GetMenuItemCount(menu1); i++)
			if (GetMenuState(menu1, i, MF_BYPOSITION)&MF_CHECKED) return i;
		return 0;
	}

	bool IsButtonChecked(int control) {
		//if (!IsWindowEnabled(GetDlgItem(hwndDlg, control))) return false;
		return IsDlgButtonChecked(hwndDlg, control) == BST_CHECKED;
	}
	
	void EnableButtons(int buttons[], int size, bool flag) {
		for(int i=0; i<size; i++){
			Button_Enable(GetDlgItem(hwndDlg, buttons[i]), flag);
		}
	}

	bool EnableButton(int ctrl, bool enable) {
		return Button_Enable(GetDlgItem(hwndDlg, ctrl), enable ? TRUE : FALSE) != 0;
	}

	bool IsButtonEnabled(int ctrl) {
		return IsWindowEnabled(GetDlgItem(hwndDlg, ctrl)) != 0;
	}

	bool IsMenuChecked(HMENU menu, int mctl) {
		return (GetMenuState(menu, mctl, MF_BYCOMMAND)&MF_CHECKED) != 0;
	}

	bool IsMenuChecked(int midx, int mctl) {
		return IsMenuChecked(GetSubMenu(GetMenu(hwndDlg), midx), mctl);
	}

	bool IsMenuEnabled(int midx, int mctl) {
		return !(GetMenuState(GetSubMenu(GetMenu(hwndDlg), midx), mctl, MF_BYCOMMAND)&MF_DISABLED);
	}

	void RadioCheckMenuItem(HMENU menu, int iitem) {
		CheckMenuRadioItem(menu, 0, GetMenuItemCount(menu) - 1, iitem, MF_BYPOSITION);
	}

	void RadioCheckMenuItem(int midx, int iitem) {
		RadioCheckMenuItem(GetSubMenu(GetMenu(hwndDlg), midx), iitem);
	}

	void EnableMenuItem(int midx, int mctl, bool enable) {
		::EnableMenuItem(GetSubMenu(GetMenu(hwndDlg), midx), mctl, MF_BYCOMMAND | (enable ? MF_ENABLED : MF_DISABLED));
	}

	WCHAR* GetRecordFile(const WCHAR *filter, const WCHAR *suffix=0) {
		OPENFILENAME ofn = {};
		ofn.lStructSize = sizeof(ofn);
		ofn.lpstrFilter = filter;
		ofn.lpstrFile = file; file[0] = 0;
		ofn.lpstrDefExt = suffix;
		ofn.nMaxFile = sizeof(file) / sizeof(WCHAR);
		ofn.Flags = OFN_OVERWRITEPROMPT | OFN_PATHMUSTEXIST | OFN_EXPLORER;
		if (!GetSaveFileName(&ofn)) file[0] = 0;
		return file;
	}

	WCHAR* GetPlaybackFile(const WCHAR *filter) {
		OPENFILENAME ofn = {};
		ofn.lStructSize = sizeof(ofn);
		ofn.lpstrFilter = filter;
		ofn.lpstrFile = file; file[0] = 0;
		ofn.nMaxFile = sizeof(file) / sizeof(WCHAR);
		ofn.Flags = OFN_FILEMUSTEXIST | OFN_PATHMUSTEXIST | OFN_EXPLORER;
		if (!GetOpenFileName(&ofn)) file[0] = 0;
		return file;
	}


protected:

	const int *controls;
	int		ncontrols;
	RECT*	layout;
	HWND	hwndDlg, panel, status;
	HMENU	menu;
	WCHAR   file[1024];

};
