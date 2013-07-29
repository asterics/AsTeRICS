{
	"patcher" : 	{
		"fileversion" : 1,
		"rect" : [ 200.0, 114.0, 427.0, 450.0 ],
		"bglocked" : 0,
		"defrect" : [ 200.0, 114.0, 427.0, 450.0 ],
		"openrect" : [ 0.0, 0.0, 0.0, 0.0 ],
		"openinpresentation" : 1,
		"default_fontsize" : 10.0,
		"default_fontface" : 0,
		"default_fontname" : "Arial",
		"gridonopen" : 0,
		"gridsize" : [ 15.0, 15.0 ],
		"gridsnaponopen" : 0,
		"toolbarvisible" : 0,
		"boxanimatetime" : 200,
		"imprint" : 0,
		"title" : "Collective Editor",
		"metadata" : [  ],
		"boxes" : [ 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "window flags grow, window exec",
					"patching_rect" : [ 158.0, 131.0, 161.0, 16.0 ],
					"fontsize" : 10.0,
					"numinlets" : 2,
					"numoutlets" : 1,
					"id" : "obj-23",
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "loadbang",
					"patching_rect" : [ 49.0, 121.0, 53.0, 18.0 ],
					"fontsize" : 10.0,
					"numinlets" : 1,
					"numoutlets" : 1,
					"id" : "obj-21",
					"outlettype" : [ "bang" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "window flags nozoom, window flags nogrow, window exec, savewindow 1",
					"patching_rect" : [ 49.0, 165.0, 349.0, 16.0 ],
					"fontsize" : 10.0,
					"numinlets" : 2,
					"numoutlets" : 1,
					"id" : "obj-20",
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "thispatcher",
					"patching_rect" : [ 39.0, 212.0, 59.0, 18.0 ],
					"fontsize" : 10.0,
					"numinlets" : 1,
					"numoutlets" : 2,
					"id" : "obj-8",
					"outlettype" : [ "", "" ],
					"fontname" : "Arial",
					"save" : [ "#N", "thispatcher", ";", "#Q", "window", "flags", "grow", "close", "nozoom", "nofloat", "menu", "minimize", ";", "#Q", "window", "constrain", 2, 2, 32768, 32768, ";", "#Q", "window", "size", 200, 114, 626, 565, ";", "#Q", "window", "title", ";", "#Q", "window", "exec", ";", "#Q", "savewindow", 1, ";", "#Q", "end", ";" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "window size 100 100 520 550, window exec",
					"hidden" : 1,
					"patching_rect" : [ 24.0, 590.0, 217.0, 15.0 ],
					"fontsize" : 8.998901,
					"numinlets" : 2,
					"numoutlets" : 1,
					"id" : "obj-16",
					"outlettype" : [ "" ],
					"fontname" : "Geneva"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "window flags nogrow, savewindow 1, window exec",
					"hidden" : 1,
					"patching_rect" : [ 63.0, 644.0, 249.0, 15.0 ],
					"fontsize" : 8.998901,
					"numinlets" : 2,
					"numoutlets" : 1,
					"id" : "obj-17",
					"outlettype" : [ "" ],
					"fontname" : "Geneva"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "window flags grow, window exec",
					"hidden" : 1,
					"patching_rect" : [ 24.0, 606.0, 163.0, 15.0 ],
					"fontsize" : 8.998901,
					"numinlets" : 2,
					"numoutlets" : 1,
					"id" : "obj-18",
					"outlettype" : [ "" ],
					"fontname" : "Geneva"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "thispatcher",
					"hidden" : 1,
					"patching_rect" : [ 24.0, 679.0, 127.0, 17.0 ],
					"fontsize" : 8.998901,
					"numinlets" : 1,
					"color" : [ 0.733333, 0.815686, 0.627451, 1.0 ],
					"numoutlets" : 2,
					"id" : "obj-19",
					"outlettype" : [ "", "" ],
					"fontname" : "Geneva",
					"save" : [ "#N", "thispatcher", ";", "#Q", "window", "flags", "grow", "close", "nozoom", "nofloat", "menu", "minimize", ";", "#Q", "window", "constrain", 2, 2, 32768, 32768, ";", "#Q", "window", "size", 200, 114, 626, 565, ";", "#Q", "window", "title", ";", "#Q", "window", "exec", ";", "#Q", "savewindow", 1, ";", "#Q", "end", ";" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"varname" : "__colldialog_message__",
					"patching_rect" : [ 92.0, 493.0, 235.0, 18.0 ],
					"fontsize" : 10.0,
					"presentation" : 1,
					"numinlets" : 1,
					"numoutlets" : 0,
					"id" : "obj-37",
					"fontname" : "Arial",
					"presentation_rect" : [ 19.0, 414.0, 245.0, 18.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : ";\r\ncolldialog includefile",
					"linecount" : 2,
					"patching_rect" : [ 10.0, 550.0, 115.0, 31.0 ],
					"fontsize" : 11.595187,
					"numinlets" : 2,
					"numoutlets" : 1,
					"id" : "obj-15",
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : ";\r\ncolldialog includefolder",
					"linecount" : 2,
					"patching_rect" : [ 8.0, 461.0, 129.0, 31.0 ],
					"fontsize" : 11.595187,
					"numinlets" : 2,
					"numoutlets" : 1,
					"id" : "obj-14",
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : ";\r\ncolldialog openscript",
					"linecount" : 2,
					"patching_rect" : [ 147.0, 530.0, 108.0, 31.0 ],
					"fontsize" : 11.595187,
					"numinlets" : 2,
					"numoutlets" : 1,
					"id" : "obj-12",
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : ";\r\ncolldialog toplevelpatcher",
					"linecount" : 2,
					"patching_rect" : [ 156.0, 465.0, 143.0, 31.0 ],
					"fontsize" : 11.595187,
					"numinlets" : 2,
					"numoutlets" : 1,
					"id" : "obj-13",
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : ";\r\ncolldialog patcher",
					"linecount" : 2,
					"patching_rect" : [ 293.0, 562.0, 101.0, 31.0 ],
					"fontsize" : 11.595187,
					"numinlets" : 2,
					"numoutlets" : 1,
					"id" : "obj-11",
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : ";\r\ncolldialog savescript",
					"linecount" : 2,
					"patching_rect" : [ 292.0, 511.0, 115.0, 31.0 ],
					"fontsize" : 11.595187,
					"numinlets" : 2,
					"numoutlets" : 1,
					"id" : "obj-10",
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : ";\r\ncolldialog build",
					"linecount" : 2,
					"patching_rect" : [ 302.0, 471.0, 91.0, 31.0 ],
					"fontsize" : 11.595187,
					"numinlets" : 2,
					"numoutlets" : 1,
					"id" : "obj-9",
					"outlettype" : [ "" ],
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"varname" : "__colldialog_scriptname__",
					"text" : "Script",
					"patching_rect" : [ 20.0, 6.0, 249.0, 20.0 ],
					"fontsize" : 12.0,
					"presentation" : 1,
					"numinlets" : 1,
					"numoutlets" : 0,
					"id" : "obj-39",
					"fontname" : "Arial",
					"presentation_rect" : [ 23.0, 9.0, 249.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "textedit",
					"varname" : "__colldialog_textedit__",
					"patching_rect" : [ 20.0, 26.0, 390.0, 300.0 ],
					"rounded" : 10.0,
					"fontsize" : 9.998779,
					"presentation" : 1,
					"numinlets" : 1,
					"numoutlets" : 3,
					"id" : "obj-38",
					"outlettype" : [ "", "int", "" ],
					"fontname" : "Geneva",
					"presentation_rect" : [ 16.0, 30.0, 390.0, 300.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "textbutton",
					"patching_rect" : [ 287.0, 422.0, 126.405739, 21.3333 ],
					"fontsize" : 11.595187,
					"presentation" : 1,
					"text" : "Build",
					"numinlets" : 1,
					"background" : 1,
					"numoutlets" : 3,
					"id" : "obj-7",
					"outlettype" : [ "int", "", "int" ],
					"fontname" : "Arial",
					"presentation_rect" : [ 283.0, 414.0, 126.405739, 21.3333 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "textbutton",
					"patching_rect" : [ 290.0, 374.0, 126.405739, 21.3333 ],
					"fontsize" : 11.595187,
					"presentation" : 1,
					"text" : "Save Script...",
					"numinlets" : 1,
					"background" : 1,
					"numoutlets" : 3,
					"id" : "obj-6",
					"outlettype" : [ "int", "", "int" ],
					"fontname" : "Arial",
					"presentation_rect" : [ 286.0, 367.0, 126.405739, 21.3333 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "textbutton",
					"patching_rect" : [ 147.0, 378.0, 126.405739, 21.3333 ],
					"fontsize" : 11.595187,
					"presentation" : 1,
					"text" : "Open Script...",
					"numinlets" : 1,
					"background" : 1,
					"numoutlets" : 3,
					"id" : "obj-5",
					"outlettype" : [ "int", "", "int" ],
					"fontname" : "Arial",
					"presentation_rect" : [ 153.0, 367.0, 126.405739, 21.3333 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "textbutton",
					"patching_rect" : [ 21.0, 373.0, 126.405739, 21.3333 ],
					"fontsize" : 11.595187,
					"presentation" : 1,
					"text" : "Include File...",
					"numinlets" : 1,
					"background" : 1,
					"numoutlets" : 3,
					"id" : "obj-4",
					"outlettype" : [ "int", "", "int" ],
					"fontname" : "Arial",
					"presentation_rect" : [ 17.0, 367.0, 126.405739, 21.3333 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "textbutton",
					"patching_rect" : [ 289.0, 340.0, 126.405739, 21.3333 ],
					"fontsize" : 11.595187,
					"presentation" : 1,
					"text" : "Patcher...",
					"numinlets" : 1,
					"background" : 1,
					"numoutlets" : 3,
					"id" : "obj-3",
					"outlettype" : [ "int", "", "int" ],
					"fontname" : "Arial",
					"presentation_rect" : [ 285.0, 339.0, 126.405739, 21.3333 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "textbutton",
					"patching_rect" : [ 155.0, 340.0, 126.405739, 21.3333 ],
					"fontsize" : 11.595187,
					"presentation" : 1,
					"text" : "Toplevel Patcher...",
					"numinlets" : 1,
					"background" : 1,
					"numoutlets" : 3,
					"id" : "obj-2",
					"outlettype" : [ "int", "", "int" ],
					"fontname" : "Arial",
					"presentation_rect" : [ 151.0, 339.0, 126.405739, 21.3333 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "textbutton",
					"patching_rect" : [ 21.0, 339.0, 126.405739, 21.3333 ],
					"fontsize" : 11.595187,
					"presentation" : 1,
					"text" : "Include Folder...",
					"numinlets" : 1,
					"background" : 1,
					"numoutlets" : 3,
					"id" : "obj-1",
					"outlettype" : [ "int", "", "int" ],
					"fontname" : "Arial",
					"presentation_rect" : [ 17.0, 339.0, 126.405739, 21.3333 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"patching_rect" : [ 37.0, 721.0, 410.0, 35.0 ],
					"rounded" : 15,
					"presentation" : 1,
					"numinlets" : 1,
					"background" : 1,
					"numoutlets" : 0,
					"id" : "obj-49",
					"bgcolor" : [ 1.0, 0.913725, 0.615686, 1.0 ],
					"presentation_rect" : [ 5.0, 404.0, 416.0, 40.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"patching_rect" : [ 95.0, 116.0, 410.0, 400.0 ],
					"rounded" : 15,
					"presentation" : 1,
					"numinlets" : 1,
					"background" : 1,
					"numoutlets" : 0,
					"id" : "obj-48",
					"bgcolor" : [ 0.85098, 0.858824, 0.964706, 1.0 ],
					"presentation_rect" : [ 5.0, 4.0, 417.0, 395.0 ]
				}

			}
 ],
		"lines" : [ 			{
				"patchline" : 				{
					"source" : [ "obj-23", 0 ],
					"destination" : [ "obj-8", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-17", 0 ],
					"destination" : [ "obj-19", 0 ],
					"hidden" : 1,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-18", 0 ],
					"destination" : [ "obj-19", 0 ],
					"hidden" : 1,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-16", 0 ],
					"destination" : [ "obj-19", 0 ],
					"hidden" : 1,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-4", 0 ],
					"destination" : [ "obj-15", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-1", 0 ],
					"destination" : [ "obj-14", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-5", 0 ],
					"destination" : [ "obj-12", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 0 ],
					"destination" : [ "obj-13", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-3", 0 ],
					"destination" : [ "obj-11", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-6", 0 ],
					"destination" : [ "obj-10", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 0 ],
					"destination" : [ "obj-9", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-20", 0 ],
					"destination" : [ "obj-8", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-21", 0 ],
					"destination" : [ "obj-20", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
 ]
	}

}
