{
	"patcher" : 	{
		"fileversion" : 1,
		"rect" : [ 533.0, 50.0, 267.0, 390.0 ],
		"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
		"bglocked" : 1,
		"defrect" : [ 533.0, 50.0, 267.0, 390.0 ],
		"openrect" : [ 0.0, 0.0, 0.0, 0.0 ],
		"openinpresentation" : 1,
		"default_fontsize" : 9.0,
		"default_fontface" : 0,
		"default_fontname" : "Arial",
		"gridonopen" : 0,
		"gridsize" : [ 15.0, 15.0 ],
		"gridsnaponopen" : 0,
		"toolbarvisible" : 0,
		"boxanimatetime" : 500,
		"imprint" : 1,
		"enablehscroll" : 1,
		"enablevscroll" : 1,
		"devicewidth" : 0.0,
		"title" : "DSP Status",
		"boxes" : [ 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "substitute append text",
					"id" : "obj-6",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 771.0, 52.0, 113.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus optionname 3",
					"id" : "obj-7",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 771.0, 30.0, 116.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "textbutton",
					"hint" : "",
					"spacing_y" : 4.0,
					"textoncolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"id" : "obj-72",
					"presentation_rect" : [ 14.0, 102.0, 91.0, 20.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"texton" : "Button On",
					"outputmode" : 1,
					"active" : 1,
					"fontlink" : 0,
					"textovercolor" : [ 0.101961, 0.101961, 0.101961, 0.0 ],
					"bordercolor" : [ 0.6, 0.6, 0.6, 0.0 ],
					"ignoreclick" : 1,
					"patching_rect" : [ 771.0, 74.0, 112.0, 17.0 ],
					"numinlets" : 1,
					"bgovercolor" : [ 0.698039, 0.698039, 0.698039, 0.0 ],
					"truncate" : 1,
					"fontface" : 1,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"tosymbol" : 1,
					"bgcolor" : [ 0.74902, 0.74902, 0.74902, 0.0 ],
					"hidden" : 0,
					"rounded" : 0.0,
					"underline" : 0,
					"borderoncolor" : [ 0.4, 0.4, 0.4, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"bgoveroncolor" : [ 0.5, 0.5, 0.5, 1.0 ],
					"align" : 0,
					"mode" : 0,
					"textoveroncolor" : [ 0.9, 0.9, 0.9, 1.0 ],
					"border" : 0,
					"spacing_x" : 4.0,
					"outlettype" : [ "", "", "int" ],
					"bgoncolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"text" : "Playthrough Input",
					"blinktime" : 150
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus option 3",
					"id" : "obj-87",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 771.0, 126.0, 90.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"hint" : "",
					"prototypename" : "Arial9",
					"discolor" : [ 0.43, 0.43, 0.43, 1.0 ],
					"depth" : 0,
					"pattrmode" : 1,
					"labelclick" : 0,
					"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
					"id" : "obj-90",
					"presentation_rect" : [ 108.0, 103.0, 139.939545, 17.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
					"arrowlink" : 0,
					"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
					"prefix_mode" : 2,
					"ignoreclick" : 0,
					"textcolor2" : [ 0.15, 0.15, 0.15, 1.0 ],
					"types" : [  ],
					"arrow" : 1,
					"patching_rect" : [ 771.0, 105.0, 50.939552, 17.0 ],
					"numinlets" : 1,
					"truncate" : 1,
					"autopopulate" : 0,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"hidden" : 0,
					"rounded" : 8,
					"underline" : 0,
					"togcolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"align" : 0,
					"menumode" : 0,
					"bgcolor2" : [ 1.0, 1.0, 1.0, 1.0 ],
					"items" : "Unsupported",
					"arrowframe" : 0,
					"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"outlettype" : [ "int", "", "" ],
					"showdotfiles" : 0,
					"prefix" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "toggle",
					"hint" : "",
					"prototypename" : "Arial9",
					"id" : "obj-84",
					"presentation_rect" : [ 48.0, 31.0, 18.0, 18.0 ],
					"background" : 0,
					"checkedcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"bordercolor" : [ 0.5, 0.5, 0.5, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 74.0, 52.0, 18.0, 18.0 ],
					"numinlets" : 1,
					"presentation" : 1,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 1,
					"numoutlets" : 1,
					"outlettype" : [ "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "View",
					"id" : "obj-115",
					"presentation_rect" : [ 67.0, 30.0, 40.0, 21.0 ],
					"fontname" : "Arial Bold Italic",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 64.0, 27.0, 40.0, 21.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 12.754706,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 1,
					"underline" : 0,
					"textcolor" : [ 1.0, 0.501961, 0.0, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 1.0, 0.501961, 0.0, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "toggle",
					"hint" : "",
					"prototypename" : "Arial9",
					"id" : "obj-66",
					"presentation_rect" : [ 145.0, 261.0, 18.0, 18.0 ],
					"background" : 0,
					"checkedcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"bordercolor" : [ 0.5, 0.5, 0.5, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 111.0, 52.0, 18.0, 18.0 ],
					"numinlets" : 1,
					"presentation" : 1,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 1,
					"numoutlets" : 1,
					"outlettype" : [ "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "NoFloat",
					"id" : "obj-78",
					"presentation_rect" : [ 164.0, 260.0, 58.0, 21.0 ],
					"fontname" : "Arial Bold Italic",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 100.0, 27.0, 58.0, 21.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 12.754706,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 1,
					"underline" : 0,
					"textcolor" : [ 1.0, 0.501961, 0.0, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 1.0, 0.501961, 0.0, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "onecopy",
					"id" : "obj-85",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 74.0, 120.0, 50.0, 18.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "thispatcher",
					"id" : "obj-142",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 74.0, 98.0, 62.0, 18.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "" ],
					"save" : [ "#N", "thispatcher", ";", "#Q", "window", "flags", "nogrow", "close", "nozoom", "float", "menu", "minimize", ";", "#Q", "window", "constrain", 50, 50, 32768, 32768, ";", "#Q", "window", "size", 533, 50, 800, 440, ";", "#Q", "window", "title", ";", "#Q", "window", "exec", ";", "#Q", "savewindow", 1, ";", "#Q", "end", ";" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "p View",
					"id" : "obj-140",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 74.0, 74.0, 56.0, 20.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 11.595187,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 1,
					"outlettype" : [ "" ],
					"patcher" : 					{
						"fileversion" : 1,
						"rect" : [ 329.0, 303.0, 657.0, 488.0 ],
						"bglocked" : 0,
						"defrect" : [ 329.0, 303.0, 657.0, 488.0 ],
						"openrect" : [ 0.0, 0.0, 0.0, 0.0 ],
						"openinpresentation" : 0,
						"default_fontsize" : 9.0,
						"default_fontface" : 0,
						"default_fontname" : "Arial",
						"gridonopen" : 0,
						"gridsize" : [ 15.0, 15.0 ],
						"gridsnaponopen" : 0,
						"toolbarvisible" : 1,
						"boxanimatetime" : 200,
						"imprint" : 0,
						"enablehscroll" : 1,
						"enablevscroll" : 1,
						"devicewidth" : 0.0,
						"boxes" : [ 							{
								"box" : 								{
									"maxclass" : "message",
									"text" : "window flags nogrow, window flags nozoom",
									"linecount" : 2,
									"id" : "obj-29",
									"fontname" : "Arial",
									"patching_rect" : [ 310.0, 343.0, 113.0, 28.0 ],
									"numinlets" : 2,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "t l",
									"id" : "obj-13",
									"fontname" : "Arial",
									"patching_rect" : [ 466.0, 378.0, 19.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "inlet",
									"prototypename" : "Arial9",
									"id" : "obj-5",
									"patching_rect" : [ 466.0, 257.0, 18.0, 18.0 ],
									"numinlets" : 0,
									"numoutlets" : 1,
									"outlettype" : [ "int" ],
									"comment" : ""
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "toggle",
									"prototypename" : "Arial9",
									"id" : "obj-6",
									"patching_rect" : [ 466.0, 279.0, 18.0, 18.0 ],
									"numinlets" : 1,
									"numoutlets" : 1,
									"outlettype" : [ "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "sel 1 0",
									"id" : "obj-8",
									"fontname" : "Arial",
									"patching_rect" : [ 466.0, 301.0, 46.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 3,
									"outlettype" : [ "bang", "bang", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "message",
									"text" : "savewindow 1",
									"id" : "obj-1",
									"fontname" : "Arial",
									"patching_rect" : [ 362.0, 428.0, 76.0, 16.0 ],
									"numinlets" : 2,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "qlim",
									"id" : "obj-63",
									"fontname" : "Arial",
									"patching_rect" : [ 43.0, 301.0, 32.5, 18.0 ],
									"numinlets" : 2,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "qlim",
									"id" : "obj-62",
									"fontname" : "Arial",
									"patching_rect" : [ 283.0, 301.0, 32.5, 18.0 ],
									"numinlets" : 2,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "t l",
									"id" : "obj-37",
									"fontname" : "Arial",
									"patching_rect" : [ 43.0, 378.0, 19.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "message",
									"text" : "window flags nofloat, window exec",
									"id" : "obj-38",
									"fontname" : "Arial",
									"patching_rect" : [ 466.0, 323.0, 170.0, 16.0 ],
									"numinlets" : 2,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "message",
									"text" : "window exec",
									"id" : "obj-39",
									"fontname" : "Arial",
									"patching_rect" : [ 43.0, 323.0, 70.0, 16.0 ],
									"numinlets" : 2,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "t b l b",
									"id" : "obj-40",
									"fontname" : "Arial",
									"patching_rect" : [ 43.0, 279.0, 46.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 3,
									"outlettype" : [ "bang", "", "bang" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "message",
									"text" : "window flags grow, window flags zoom",
									"linecount" : 2,
									"id" : "obj-42",
									"fontname" : "Arial",
									"patching_rect" : [ 70.0, 343.0, 100.0, 28.0 ],
									"numinlets" : 2,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "prepend window size",
									"id" : "obj-43",
									"fontname" : "Arial",
									"patching_rect" : [ 43.0, 257.0, 108.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "Ymax",
									"id" : "obj-45",
									"fontname" : "Arial",
									"patching_rect" : [ 208.0, 186.0, 37.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 0
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "Xmax",
									"id" : "obj-46",
									"fontname" : "Arial",
									"patching_rect" : [ 157.0, 186.0, 37.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 0
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "Ymin",
									"id" : "obj-47",
									"fontname" : "Arial",
									"patching_rect" : [ 105.0, 186.0, 34.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 0
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "Xmin",
									"id" : "obj-48",
									"fontname" : "Arial",
									"patching_rect" : [ 54.0, 186.0, 34.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 0
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "pref.",
									"id" : "obj-49",
									"fontname" : "Arial",
									"patching_rect" : [ 171.0, 147.0, 31.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 0
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "unpack 0 0 0 0",
									"id" : "obj-51",
									"fontname" : "Arial",
									"patching_rect" : [ 43.0, 167.0, 173.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 4,
									"outlettype" : [ "int", "int", "int", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "message",
									"text" : "150 50 1060 610",
									"id" : "obj-52",
									"fontname" : "Arial",
									"patching_rect" : [ 43.0, 147.0, 127.0, 16.0 ],
									"numinlets" : 2,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "number",
									"id" : "obj-53",
									"fontname" : "Arial",
									"patching_rect" : [ 197.0, 205.0, 50.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 2,
									"outlettype" : [ "int", "bang" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "number",
									"id" : "obj-54",
									"fontname" : "Arial",
									"patching_rect" : [ 146.0, 205.0, 50.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 2,
									"outlettype" : [ "int", "bang" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "pak 0 0 0 0",
									"id" : "obj-55",
									"fontname" : "Arial",
									"patching_rect" : [ 43.0, 227.0, 173.0, 18.0 ],
									"numinlets" : 4,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "number",
									"id" : "obj-56",
									"fontname" : "Arial",
									"patching_rect" : [ 94.0, 205.0, 51.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 2,
									"outlettype" : [ "int", "bang" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "number",
									"id" : "obj-57",
									"fontname" : "Arial",
									"patching_rect" : [ 43.0, 205.0, 50.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 2,
									"outlettype" : [ "int", "bang" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "t l",
									"id" : "obj-35",
									"fontname" : "Arial",
									"patching_rect" : [ 283.0, 378.0, 19.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "message",
									"text" : "window flags float, window exec",
									"id" : "obj-32",
									"fontname" : "Arial",
									"patching_rect" : [ 480.0, 343.0, 158.0, 16.0 ],
									"numinlets" : 2,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "message",
									"text" : "window exec",
									"id" : "obj-31",
									"fontname" : "Arial",
									"patching_rect" : [ 283.0, 323.0, 70.0, 16.0 ],
									"numinlets" : 2,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "t b l b",
									"id" : "obj-30",
									"fontname" : "Arial",
									"patching_rect" : [ 283.0, 279.0, 46.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 3,
									"outlettype" : [ "bang", "", "bang" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "prepend window size",
									"id" : "obj-28",
									"fontname" : "Arial",
									"patching_rect" : [ 283.0, 257.0, 108.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "Ymax",
									"id" : "obj-27",
									"fontname" : "Arial",
									"patching_rect" : [ 448.0, 186.0, 37.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 0
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "Xmax",
									"id" : "obj-26",
									"fontname" : "Arial",
									"patching_rect" : [ 397.0, 186.0, 37.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 0
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "Ymin",
									"id" : "obj-23",
									"fontname" : "Arial",
									"patching_rect" : [ 345.0, 186.0, 34.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 0
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "Xmin",
									"id" : "obj-22",
									"fontname" : "Arial",
									"patching_rect" : [ 294.0, 186.0, 34.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 0
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "pref.",
									"id" : "obj-20",
									"fontname" : "Arial",
									"patching_rect" : [ 411.0, 147.0, 31.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 0
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "unpack 0 0 0 0",
									"id" : "obj-18",
									"fontname" : "Arial",
									"patching_rect" : [ 283.0, 167.0, 173.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 4,
									"outlettype" : [ "int", "int", "int", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "message",
									"text" : "533 50 800 440",
									"id" : "obj-16",
									"fontname" : "Arial",
									"patching_rect" : [ 283.0, 147.0, 127.0, 16.0 ],
									"numinlets" : 2,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "number",
									"id" : "obj-12",
									"fontname" : "Arial",
									"patching_rect" : [ 437.0, 205.0, 50.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 2,
									"outlettype" : [ "int", "bang" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "number",
									"id" : "obj-15",
									"fontname" : "Arial",
									"patching_rect" : [ 386.0, 205.0, 50.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 2,
									"outlettype" : [ "int", "bang" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "pak 0 0 0 0",
									"id" : "obj-7",
									"fontname" : "Arial",
									"patching_rect" : [ 283.0, 227.0, 173.0, 18.0 ],
									"numinlets" : 4,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "number",
									"id" : "obj-4",
									"fontname" : "Arial",
									"patching_rect" : [ 334.0, 205.0, 51.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 2,
									"outlettype" : [ "int", "bang" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "number",
									"id" : "obj-3",
									"fontname" : "Arial",
									"patching_rect" : [ 283.0, 205.0, 50.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 2,
									"outlettype" : [ "int", "bang" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "view",
									"id" : "obj-11",
									"fontname" : "Arial",
									"patching_rect" : [ 284.0, 69.0, 31.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 0
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "message",
									"text" : "presentation $1",
									"id" : "obj-10",
									"fontname" : "Arial",
									"patching_rect" : [ 250.0, 90.0, 82.0, 16.0 ],
									"numinlets" : 2,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "== 0",
									"id" : "obj-9",
									"fontname" : "Arial",
									"patching_rect" : [ 250.0, 69.0, 32.5, 18.0 ],
									"numinlets" : 2,
									"fontsize" : 10.435669,
									"numoutlets" : 1,
									"outlettype" : [ "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "inlet",
									"prototypename" : "Arial9",
									"id" : "obj-44",
									"patching_rect" : [ 153.0, 17.0, 18.0, 18.0 ],
									"numinlets" : 0,
									"numoutlets" : 1,
									"outlettype" : [ "int" ],
									"comment" : ""
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "toggle",
									"prototypename" : "Arial9",
									"id" : "obj-41",
									"patching_rect" : [ 153.0, 52.0, 18.0, 18.0 ],
									"numinlets" : 1,
									"numoutlets" : 1,
									"outlettype" : [ "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "sel 1 0",
									"id" : "obj-25",
									"fontname" : "Arial",
									"patching_rect" : [ 153.0, 74.0, 46.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 3,
									"outlettype" : [ "bang", "bang", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "outlet",
									"prototypename" : "Arial9",
									"id" : "obj-21",
									"patching_rect" : [ 250.0, 443.0, 18.0, 18.0 ],
									"numinlets" : 1,
									"numoutlets" : 0,
									"comment" : ""
								}

							}
 ],
						"lines" : [ 							{
								"patchline" : 								{
									"source" : [ "obj-41", 0 ],
									"destination" : [ "obj-25", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-44", 0 ],
									"destination" : [ "obj-41", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-41", 0 ],
									"destination" : [ "obj-9", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-9", 0 ],
									"destination" : [ "obj-10", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-10", 0 ],
									"destination" : [ "obj-21", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-3", 0 ],
									"destination" : [ "obj-7", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-4", 0 ],
									"destination" : [ "obj-7", 1 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-15", 0 ],
									"destination" : [ "obj-7", 2 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-12", 0 ],
									"destination" : [ "obj-7", 3 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-18", 0 ],
									"destination" : [ "obj-3", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-18", 1 ],
									"destination" : [ "obj-4", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-18", 2 ],
									"destination" : [ "obj-15", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-18", 3 ],
									"destination" : [ "obj-12", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-16", 0 ],
									"destination" : [ "obj-18", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-7", 0 ],
									"destination" : [ "obj-16", 1 ],
									"hidden" : 0,
									"midpoints" : [ 292.5, 251.0, 274.0, 251.0, 274.0, 139.0, 400.5, 139.0 ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-31", 0 ],
									"destination" : [ "obj-35", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-29", 0 ],
									"destination" : [ "obj-35", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-35", 0 ],
									"destination" : [ "obj-21", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-42", 0 ],
									"destination" : [ "obj-37", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-39", 0 ],
									"destination" : [ "obj-37", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-55", 0 ],
									"destination" : [ "obj-52", 1 ],
									"hidden" : 0,
									"midpoints" : [ 52.5, 251.0, 34.0, 251.0, 34.0, 139.0, 160.5, 139.0 ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-52", 0 ],
									"destination" : [ "obj-51", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-51", 3 ],
									"destination" : [ "obj-53", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-51", 2 ],
									"destination" : [ "obj-54", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-51", 1 ],
									"destination" : [ "obj-56", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-51", 0 ],
									"destination" : [ "obj-57", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-53", 0 ],
									"destination" : [ "obj-55", 3 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-54", 0 ],
									"destination" : [ "obj-55", 2 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-56", 0 ],
									"destination" : [ "obj-55", 1 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-57", 0 ],
									"destination" : [ "obj-55", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-37", 0 ],
									"destination" : [ "obj-21", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-55", 0 ],
									"destination" : [ "obj-43", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-7", 0 ],
									"destination" : [ "obj-28", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-62", 0 ],
									"destination" : [ "obj-31", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-63", 0 ],
									"destination" : [ "obj-39", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-25", 0 ],
									"destination" : [ "obj-52", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-25", 1 ],
									"destination" : [ "obj-16", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-1", 0 ],
									"destination" : [ "obj-21", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-30", 0 ],
									"destination" : [ "obj-62", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-30", 1 ],
									"destination" : [ "obj-35", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-30", 2 ],
									"destination" : [ "obj-29", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-28", 0 ],
									"destination" : [ "obj-30", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-5", 0 ],
									"destination" : [ "obj-6", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-6", 0 ],
									"destination" : [ "obj-8", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-8", 0 ],
									"destination" : [ "obj-38", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-40", 0 ],
									"destination" : [ "obj-63", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-43", 0 ],
									"destination" : [ "obj-40", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-40", 2 ],
									"destination" : [ "obj-42", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-40", 1 ],
									"destination" : [ "obj-37", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-8", 1 ],
									"destination" : [ "obj-32", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-38", 0 ],
									"destination" : [ "obj-13", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-32", 0 ],
									"destination" : [ "obj-13", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-13", 0 ],
									"destination" : [ "obj-21", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
 ]
					}
,
					"saved_object_attributes" : 					{
						"default_fontname" : "Arial",
						"fontname" : "Arial",
						"default_fontsize" : 9.0,
						"globalpatchername" : "",
						"fontface" : 0,
						"fontsize" : 9.0,
						"default_fontface" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "p \"I/O Mappings\"",
					"id" : "obj-114",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 425.0, 501.0, 89.0, 18.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 0,
					"patcher" : 					{
						"fileversion" : 1,
						"rect" : [ 806.0, 81.0, 194.0, 351.0 ],
						"bglocked" : 1,
						"defrect" : [ 806.0, 81.0, 194.0, 351.0 ],
						"openrect" : [ 0.0, 0.0, 0.0, 0.0 ],
						"openinpresentation" : 1,
						"default_fontsize" : 9.0,
						"default_fontface" : 0,
						"default_fontname" : "Arial",
						"gridonopen" : 0,
						"gridsize" : [ 15.0, 15.0 ],
						"gridsnaponopen" : 0,
						"toolbarvisible" : 0,
						"boxanimatetime" : 200,
						"imprint" : 0,
						"enablehscroll" : 1,
						"enablevscroll" : 1,
						"devicewidth" : 0.0,
						"title" : "I/O Mappings",
						"boxes" : [ 							{
								"box" : 								{
									"maxclass" : "toggle",
									"prototypename" : "Arial9",
									"id" : "obj-84",
									"presentation_rect" : [ 6.0, 4.0, 18.0, 18.0 ],
									"patching_rect" : [ 34.0, 48.0, 18.0, 18.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"hidden" : 1,
									"numoutlets" : 1,
									"outlettype" : [ "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "View",
									"id" : "obj-115",
									"presentation_rect" : [ 27.0, 3.0, 41.0, 21.0 ],
									"fontname" : "Arial Bold Italic",
									"patching_rect" : [ 23.0, 23.0, 41.0, 21.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 12.754706,
									"hidden" : 1,
									"textcolor" : [ 1.0, 0.501961, 0.0, 1.0 ],
									"numoutlets" : 0,
									"frgb" : [ 1.0, 0.501961, 0.0, 1.0 ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "toggle",
									"prototypename" : "Arial9",
									"id" : "obj-237",
									"presentation_rect" : [ 96.0, 4.0, 18.0, 18.0 ],
									"patching_rect" : [ 140.0, 48.0, 18.0, 18.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"hidden" : 1,
									"numoutlets" : 1,
									"outlettype" : [ "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "NoFloat",
									"id" : "obj-78",
									"presentation_rect" : [ 115.0, 3.0, 58.0, 21.0 ],
									"fontname" : "Arial Bold Italic",
									"patching_rect" : [ 129.0, 23.0, 56.0, 21.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 12.754706,
									"hidden" : 1,
									"textcolor" : [ 1.0, 0.501961, 0.0, 1.0 ],
									"numoutlets" : 0,
									"frgb" : [ 1.0, 0.501961, 0.0, 1.0 ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "thispatcher",
									"id" : "obj-238",
									"fontname" : "Arial",
									"patching_rect" : [ 70.0, 47.0, 62.0, 18.0 ],
									"numinlets" : 1,
									"fontsize" : 10.435669,
									"numoutlets" : 2,
									"outlettype" : [ "", "" ],
									"save" : [ "#N", "thispatcher", ";", "#Q", "window", "flags", "nogrow", "close", "nozoom", "float", "menu", "minimize", ";", "#Q", "window", "constrain", 50, 50, 32768, 32768, ";", "#Q", "window", "size", 806, 81, 1000, 432, ";", "#Q", "window", "title", ";", "#Q", "window", "exec", ";", "#Q", "savewindow", 1, ";", "#Q", "end", ";" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "p View",
									"id" : "obj-239",
									"fontname" : "Arial",
									"patching_rect" : [ 70.0, 23.0, 56.0, 20.0 ],
									"numinlets" : 2,
									"fontsize" : 11.595187,
									"numoutlets" : 1,
									"outlettype" : [ "" ],
									"patcher" : 									{
										"fileversion" : 1,
										"rect" : [ 70.0, 262.0, 657.0, 488.0 ],
										"bglocked" : 0,
										"defrect" : [ 70.0, 262.0, 657.0, 488.0 ],
										"openrect" : [ 0.0, 0.0, 0.0, 0.0 ],
										"openinpresentation" : 0,
										"default_fontsize" : 9.0,
										"default_fontface" : 0,
										"default_fontname" : "Arial",
										"gridonopen" : 0,
										"gridsize" : [ 15.0, 15.0 ],
										"gridsnaponopen" : 0,
										"toolbarvisible" : 1,
										"boxanimatetime" : 200,
										"imprint" : 0,
										"enablehscroll" : 1,
										"enablevscroll" : 1,
										"devicewidth" : 0.0,
										"boxes" : [ 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "loadmess 0",
													"id" : "obj-2",
													"fontname" : "Arial",
													"patching_rect" : [ 392.0, 13.0, 57.0, 17.0 ],
													"numinlets" : 1,
													"fontsize" : 9.0,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "t l",
													"id" : "obj-13",
													"fontname" : "Arial",
													"patching_rect" : [ 466.0, 378.0, 19.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "inlet",
													"prototypename" : "Arial9",
													"id" : "obj-5",
													"patching_rect" : [ 466.0, 257.0, 18.0, 18.0 ],
													"numinlets" : 0,
													"numoutlets" : 1,
													"outlettype" : [ "int" ],
													"comment" : ""
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "toggle",
													"prototypename" : "Arial9",
													"id" : "obj-6",
													"patching_rect" : [ 466.0, 279.0, 18.0, 18.0 ],
													"numinlets" : 1,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "sel 1 0",
													"id" : "obj-8",
													"fontname" : "Arial",
													"patching_rect" : [ 466.0, 301.0, 46.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 3,
													"outlettype" : [ "bang", "bang", "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "savewindow 1",
													"id" : "obj-1",
													"fontname" : "Arial",
													"patching_rect" : [ 362.0, 428.0, 76.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "qlim",
													"id" : "obj-63",
													"fontname" : "Arial",
													"patching_rect" : [ 43.0, 301.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "qlim",
													"id" : "obj-62",
													"fontname" : "Arial",
													"patching_rect" : [ 283.0, 301.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "t l",
													"id" : "obj-37",
													"fontname" : "Arial",
													"patching_rect" : [ 43.0, 378.0, 19.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "window flags nofloat, window exec",
													"id" : "obj-38",
													"fontname" : "Arial",
													"patching_rect" : [ 466.0, 323.0, 170.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "window exec",
													"id" : "obj-39",
													"fontname" : "Arial",
													"patching_rect" : [ 43.0, 323.0, 70.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "t b l b",
													"id" : "obj-40",
													"fontname" : "Arial",
													"patching_rect" : [ 43.0, 279.0, 46.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 3,
													"outlettype" : [ "bang", "", "bang" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "window flags grow, window flags zoom",
													"linecount" : 2,
													"id" : "obj-42",
													"fontname" : "Arial",
													"patching_rect" : [ 70.0, 343.0, 100.0, 28.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "prepend window size",
													"id" : "obj-43",
													"fontname" : "Arial",
													"patching_rect" : [ 43.0, 257.0, 108.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "comment",
													"text" : "Ymax",
													"id" : "obj-45",
													"fontname" : "Arial",
													"patching_rect" : [ 208.0, 186.0, 37.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 0
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "comment",
													"text" : "Xmax",
													"id" : "obj-46",
													"fontname" : "Arial",
													"patching_rect" : [ 157.0, 186.0, 37.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 0
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "comment",
													"text" : "Ymin",
													"id" : "obj-47",
													"fontname" : "Arial",
													"patching_rect" : [ 105.0, 186.0, 34.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 0
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "comment",
													"text" : "Xmin",
													"id" : "obj-48",
													"fontname" : "Arial",
													"patching_rect" : [ 54.0, 186.0, 34.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 0
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "comment",
													"text" : "pref.",
													"id" : "obj-49",
													"fontname" : "Arial",
													"patching_rect" : [ 171.0, 147.0, 31.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 0
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "unpack 0 0 0 0",
													"id" : "obj-51",
													"fontname" : "Arial",
													"patching_rect" : [ 43.0, 167.0, 173.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 4,
													"outlettype" : [ "int", "int", "int", "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "130 50 1135 520",
													"id" : "obj-52",
													"fontname" : "Arial",
													"patching_rect" : [ 43.0, 147.0, 127.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "number",
													"id" : "obj-53",
													"fontname" : "Arial",
													"patching_rect" : [ 197.0, 205.0, 50.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 2,
													"outlettype" : [ "int", "bang" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "number",
													"id" : "obj-54",
													"fontname" : "Arial",
													"patching_rect" : [ 146.0, 205.0, 50.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 2,
													"outlettype" : [ "int", "bang" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "pak 0 0 0 0",
													"id" : "obj-55",
													"fontname" : "Arial",
													"patching_rect" : [ 43.0, 227.0, 173.0, 18.0 ],
													"numinlets" : 4,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "number",
													"id" : "obj-56",
													"fontname" : "Arial",
													"patching_rect" : [ 94.0, 205.0, 51.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 2,
													"outlettype" : [ "int", "bang" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "number",
													"id" : "obj-57",
													"fontname" : "Arial",
													"patching_rect" : [ 43.0, 205.0, 50.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 2,
													"outlettype" : [ "int", "bang" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "t l",
													"id" : "obj-35",
													"fontname" : "Arial",
													"patching_rect" : [ 283.0, 378.0, 19.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "window flags float, window exec",
													"id" : "obj-32",
													"fontname" : "Arial",
													"patching_rect" : [ 480.0, 343.0, 158.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "window exec",
													"id" : "obj-31",
													"fontname" : "Arial",
													"patching_rect" : [ 283.0, 323.0, 70.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "t b l b",
													"id" : "obj-30",
													"fontname" : "Arial",
													"patching_rect" : [ 283.0, 279.0, 46.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 3,
													"outlettype" : [ "bang", "", "bang" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "window flags nogrow, window flags nozoom",
													"linecount" : 2,
													"id" : "obj-29",
													"fontname" : "Arial",
													"patching_rect" : [ 310.0, 343.0, 113.0, 28.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "prepend window size",
													"id" : "obj-28",
													"fontname" : "Arial",
													"patching_rect" : [ 283.0, 257.0, 108.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "comment",
													"text" : "Ymax",
													"id" : "obj-27",
													"fontname" : "Arial",
													"patching_rect" : [ 448.0, 186.0, 37.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 0
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "comment",
													"text" : "Xmax",
													"id" : "obj-26",
													"fontname" : "Arial",
													"patching_rect" : [ 397.0, 186.0, 37.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 0
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "comment",
													"text" : "Ymin",
													"id" : "obj-23",
													"fontname" : "Arial",
													"patching_rect" : [ 345.0, 186.0, 34.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 0
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "comment",
													"text" : "Xmin",
													"id" : "obj-22",
													"fontname" : "Arial",
													"patching_rect" : [ 294.0, 186.0, 34.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 0
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "comment",
													"text" : "pref.",
													"id" : "obj-20",
													"fontname" : "Arial",
													"patching_rect" : [ 411.0, 147.0, 31.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 0
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "unpack 0 0 0 0",
													"id" : "obj-18",
													"fontname" : "Arial",
													"patching_rect" : [ 283.0, 167.0, 173.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 4,
													"outlettype" : [ "int", "int", "int", "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "806 81 1000 432",
													"id" : "obj-16",
													"fontname" : "Arial",
													"patching_rect" : [ 283.0, 147.0, 127.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "number",
													"id" : "obj-12",
													"fontname" : "Arial",
													"patching_rect" : [ 437.0, 205.0, 50.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 2,
													"outlettype" : [ "int", "bang" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "number",
													"id" : "obj-15",
													"fontname" : "Arial",
													"patching_rect" : [ 386.0, 205.0, 50.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 2,
													"outlettype" : [ "int", "bang" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "pak 0 0 0 0",
													"id" : "obj-7",
													"fontname" : "Arial",
													"patching_rect" : [ 283.0, 227.0, 173.0, 18.0 ],
													"numinlets" : 4,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "number",
													"id" : "obj-4",
													"fontname" : "Arial",
													"patching_rect" : [ 334.0, 205.0, 51.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 2,
													"outlettype" : [ "int", "bang" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "number",
													"id" : "obj-3",
													"fontname" : "Arial",
													"patching_rect" : [ 283.0, 205.0, 50.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 2,
													"outlettype" : [ "int", "bang" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "comment",
													"text" : "view",
													"id" : "obj-11",
													"fontname" : "Arial",
													"patching_rect" : [ 284.0, 69.0, 31.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 0
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "presentation $1",
													"id" : "obj-10",
													"fontname" : "Arial",
													"patching_rect" : [ 250.0, 90.0, 82.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "== 0",
													"id" : "obj-9",
													"fontname" : "Arial",
													"patching_rect" : [ 250.0, 69.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "inlet",
													"prototypename" : "Arial9",
													"id" : "obj-44",
													"patching_rect" : [ 153.0, 17.0, 18.0, 18.0 ],
													"numinlets" : 0,
													"numoutlets" : 1,
													"outlettype" : [ "int" ],
													"comment" : ""
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "toggle",
													"prototypename" : "Arial9",
													"id" : "obj-41",
													"patching_rect" : [ 153.0, 52.0, 18.0, 18.0 ],
													"numinlets" : 1,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "sel 1 0",
													"id" : "obj-25",
													"fontname" : "Arial",
													"patching_rect" : [ 153.0, 74.0, 46.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 3,
													"outlettype" : [ "bang", "bang", "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"prototypename" : "Arial9",
													"id" : "obj-21",
													"patching_rect" : [ 250.0, 443.0, 18.0, 18.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : ""
												}

											}
 ],
										"lines" : [ 											{
												"patchline" : 												{
													"source" : [ "obj-2", 0 ],
													"destination" : [ "obj-6", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-2", 0 ],
													"destination" : [ "obj-41", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-29", 0 ],
													"destination" : [ "obj-35", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-30", 2 ],
													"destination" : [ "obj-29", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-42", 0 ],
													"destination" : [ "obj-37", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-40", 2 ],
													"destination" : [ "obj-42", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-13", 0 ],
													"destination" : [ "obj-21", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-32", 0 ],
													"destination" : [ "obj-13", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-38", 0 ],
													"destination" : [ "obj-13", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-8", 1 ],
													"destination" : [ "obj-32", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-40", 1 ],
													"destination" : [ "obj-37", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-43", 0 ],
													"destination" : [ "obj-40", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-40", 0 ],
													"destination" : [ "obj-63", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-8", 0 ],
													"destination" : [ "obj-38", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-6", 0 ],
													"destination" : [ "obj-8", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-5", 0 ],
													"destination" : [ "obj-6", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-28", 0 ],
													"destination" : [ "obj-30", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-30", 1 ],
													"destination" : [ "obj-35", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-30", 0 ],
													"destination" : [ "obj-62", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-1", 0 ],
													"destination" : [ "obj-21", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-25", 1 ],
													"destination" : [ "obj-16", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-25", 0 ],
													"destination" : [ "obj-52", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-63", 0 ],
													"destination" : [ "obj-39", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-62", 0 ],
													"destination" : [ "obj-31", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-7", 0 ],
													"destination" : [ "obj-28", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-55", 0 ],
													"destination" : [ "obj-43", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-37", 0 ],
													"destination" : [ "obj-21", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-57", 0 ],
													"destination" : [ "obj-55", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-56", 0 ],
													"destination" : [ "obj-55", 1 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-54", 0 ],
													"destination" : [ "obj-55", 2 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-53", 0 ],
													"destination" : [ "obj-55", 3 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-51", 0 ],
													"destination" : [ "obj-57", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-51", 1 ],
													"destination" : [ "obj-56", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-51", 2 ],
													"destination" : [ "obj-54", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-51", 3 ],
													"destination" : [ "obj-53", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-52", 0 ],
													"destination" : [ "obj-51", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-55", 0 ],
													"destination" : [ "obj-52", 1 ],
													"hidden" : 0,
													"midpoints" : [ 52.5, 251.0, 34.0, 251.0, 34.0, 139.0, 160.5, 139.0 ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-39", 0 ],
													"destination" : [ "obj-37", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-35", 0 ],
													"destination" : [ "obj-21", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-31", 0 ],
													"destination" : [ "obj-35", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-7", 0 ],
													"destination" : [ "obj-16", 1 ],
													"hidden" : 0,
													"midpoints" : [ 292.5, 251.0, 274.0, 251.0, 274.0, 139.0, 400.5, 139.0 ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-16", 0 ],
													"destination" : [ "obj-18", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-18", 3 ],
													"destination" : [ "obj-12", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-18", 2 ],
													"destination" : [ "obj-15", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-18", 1 ],
													"destination" : [ "obj-4", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-18", 0 ],
													"destination" : [ "obj-3", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-12", 0 ],
													"destination" : [ "obj-7", 3 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-15", 0 ],
													"destination" : [ "obj-7", 2 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-4", 0 ],
													"destination" : [ "obj-7", 1 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-3", 0 ],
													"destination" : [ "obj-7", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-10", 0 ],
													"destination" : [ "obj-21", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-9", 0 ],
													"destination" : [ "obj-10", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-41", 0 ],
													"destination" : [ "obj-9", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-44", 0 ],
													"destination" : [ "obj-41", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-41", 0 ],
													"destination" : [ "obj-25", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
 ]
									}
,
									"saved_object_attributes" : 									{
										"default_fontname" : "Arial",
										"fontname" : "Arial",
										"default_fontsize" : 9.0,
										"globalpatchername" : "",
										"fontface" : 0,
										"fontsize" : 9.0,
										"default_fontface" : 0
									}

								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-127",
									"presentation_rect" : [ 95.0, 198.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 285.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 9,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-128",
									"presentation_rect" : [ 95.0, 181.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 267.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 8,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-129",
									"presentation_rect" : [ 95.0, 164.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 249.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 7,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-130",
									"presentation_rect" : [ 95.0, 147.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 231.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 6,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-131",
									"presentation_rect" : [ 95.0, 130.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 213.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 5,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-132",
									"presentation_rect" : [ 95.0, 113.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 195.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 4,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-133",
									"presentation_rect" : [ 95.0, 96.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 177.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 3,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-134",
									"presentation_rect" : [ 95.0, 215.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 303.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 10,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-135",
									"presentation_rect" : [ 95.0, 232.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 321.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 11,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-136",
									"presentation_rect" : [ 95.0, 249.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 339.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 12,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-137",
									"presentation_rect" : [ 95.0, 266.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 357.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 13,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-138",
									"presentation_rect" : [ 95.0, 283.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 375.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 14,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-139",
									"presentation_rect" : [ 95.0, 300.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 393.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 15,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-140",
									"presentation_rect" : [ 95.0, 317.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 411.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 16,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-141",
									"presentation_rect" : [ 95.0, 79.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 159.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 2,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-142",
									"presentation_rect" : [ 95.0, 62.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 525.0, 141.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 1,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "p Ogrouplabel",
									"id" : "obj-143",
									"fontname" : "Arial",
									"patching_rect" : [ 525.0, 84.0, 221.5, 17.0 ],
									"numinlets" : 1,
									"fontsize" : 9.27615,
									"numoutlets" : 16,
									"outlettype" : [ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" ],
									"patcher" : 									{
										"fileversion" : 1,
										"rect" : [ 228.0, 171.0, 713.0, 321.0 ],
										"bglocked" : 1,
										"defrect" : [ 228.0, 171.0, 713.0, 321.0 ],
										"openrect" : [ 0.0, 0.0, 0.0, 0.0 ],
										"openinpresentation" : 0,
										"default_fontsize" : 9.0,
										"default_fontface" : 0,
										"default_fontname" : "Arial",
										"gridonopen" : 0,
										"gridsize" : [ 15.0, 15.0 ],
										"gridsnaponopen" : 0,
										"toolbarvisible" : 1,
										"boxanimatetime" : 200,
										"imprint" : 0,
										"enablehscroll" : 1,
										"enablevscroll" : 1,
										"devicewidth" : 0.0,
										"boxes" : [ 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "pack",
													"id" : "obj-1",
													"fontname" : "Arial",
													"patching_rect" : [ 55.0, 177.0, 33.0, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "- 1",
													"id" : "obj-2",
													"fontname" : "Arial",
													"patching_rect" : [ 55.0, 129.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "+ 1",
													"id" : "obj-3",
													"fontname" : "Arial",
													"patching_rect" : [ 27.0, 63.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "* 16",
													"id" : "obj-6",
													"fontname" : "Arial",
													"patching_rect" : [ 27.0, 42.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "Uzi 16",
													"id" : "obj-7",
													"fontname" : "Arial",
													"patching_rect" : [ 27.0, 107.0, 47.0, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 3,
													"outlettype" : [ "bang", "bang", "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "+",
													"id" : "obj-8",
													"fontname" : "Arial",
													"patching_rect" : [ 69.0, 155.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "t b i",
													"id" : "obj-9",
													"fontname" : "Arial",
													"patching_rect" : [ 27.0, 85.0, 74.5, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 2,
													"outlettype" : [ "bang", "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-11",
													"fontname" : "Arial",
													"patching_rect" : [ 624.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-12",
													"fontname" : "Arial",
													"patching_rect" : [ 586.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-13",
													"fontname" : "Arial",
													"patching_rect" : [ 548.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-14",
													"fontname" : "Arial",
													"patching_rect" : [ 510.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-15",
													"fontname" : "Arial",
													"patching_rect" : [ 472.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-16",
													"fontname" : "Arial",
													"patching_rect" : [ 434.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-17",
													"fontname" : "Arial",
													"patching_rect" : [ 396.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-18",
													"fontname" : "Arial",
													"patching_rect" : [ 358.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-19",
													"fontname" : "Arial",
													"patching_rect" : [ 321.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-20",
													"fontname" : "Arial",
													"patching_rect" : [ 283.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-21",
													"fontname" : "Arial",
													"patching_rect" : [ 245.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-22",
													"fontname" : "Arial",
													"patching_rect" : [ 207.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-23",
													"fontname" : "Arial",
													"patching_rect" : [ 169.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-24",
													"fontname" : "Arial",
													"patching_rect" : [ 131.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-25",
													"fontname" : "Arial",
													"patching_rect" : [ 93.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-26",
													"fontname" : "Arial",
													"patching_rect" : [ 55.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "spray 16",
													"id" : "obj-27",
													"fontname" : "Arial",
													"patching_rect" : [ 55.0, 199.0, 588.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 16,
													"outlettype" : [ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "inlet",
													"id" : "obj-28",
													"patching_rect" : [ 27.0, 15.0, 17.0, 17.0 ],
													"numinlets" : 0,
													"numoutlets" : 1,
													"outlettype" : [ "int" ],
													"comment" : "1-16"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-29",
													"patching_rect" : [ 624.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "16"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-30",
													"patching_rect" : [ 586.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "15"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-31",
													"patching_rect" : [ 548.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "14"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-32",
													"patching_rect" : [ 510.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "13"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-33",
													"patching_rect" : [ 472.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "12"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-34",
													"patching_rect" : [ 434.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "11"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-35",
													"patching_rect" : [ 396.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "10"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-36",
													"patching_rect" : [ 358.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "9"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-37",
													"patching_rect" : [ 321.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "8"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-38",
													"patching_rect" : [ 283.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "7"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-39",
													"patching_rect" : [ 245.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "6"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-40",
													"patching_rect" : [ 207.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "5"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-41",
													"patching_rect" : [ 169.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "4"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-42",
													"patching_rect" : [ 131.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "3"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-43",
													"patching_rect" : [ 93.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "2"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-44",
													"patching_rect" : [ 55.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "1"
												}

											}
 ],
										"lines" : [ 											{
												"patchline" : 												{
													"source" : [ "obj-28", 0 ],
													"destination" : [ "obj-6", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-6", 0 ],
													"destination" : [ "obj-3", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-3", 0 ],
													"destination" : [ "obj-9", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-9", 0 ],
													"destination" : [ "obj-7", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-7", 2 ],
													"destination" : [ "obj-2", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-2", 0 ],
													"destination" : [ "obj-1", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-1", 0 ],
													"destination" : [ "obj-27", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 0 ],
													"destination" : [ "obj-26", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 0 ],
													"destination" : [ "obj-44", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-2", 0 ],
													"destination" : [ "obj-8", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-8", 0 ],
													"destination" : [ "obj-1", 1 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-9", 1 ],
													"destination" : [ "obj-8", 1 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 1 ],
													"destination" : [ "obj-25", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-25", 0 ],
													"destination" : [ "obj-43", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 2 ],
													"destination" : [ "obj-24", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-24", 0 ],
													"destination" : [ "obj-42", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 3 ],
													"destination" : [ "obj-23", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-23", 0 ],
													"destination" : [ "obj-41", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 4 ],
													"destination" : [ "obj-22", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-22", 0 ],
													"destination" : [ "obj-40", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 5 ],
													"destination" : [ "obj-21", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-21", 0 ],
													"destination" : [ "obj-39", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 6 ],
													"destination" : [ "obj-20", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-20", 0 ],
													"destination" : [ "obj-38", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 7 ],
													"destination" : [ "obj-19", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-19", 0 ],
													"destination" : [ "obj-37", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 8 ],
													"destination" : [ "obj-18", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-18", 0 ],
													"destination" : [ "obj-36", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 9 ],
													"destination" : [ "obj-17", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-17", 0 ],
													"destination" : [ "obj-35", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 10 ],
													"destination" : [ "obj-16", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-16", 0 ],
													"destination" : [ "obj-34", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 11 ],
													"destination" : [ "obj-15", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-15", 0 ],
													"destination" : [ "obj-33", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 12 ],
													"destination" : [ "obj-14", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-14", 0 ],
													"destination" : [ "obj-32", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 13 ],
													"destination" : [ "obj-13", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-13", 0 ],
													"destination" : [ "obj-31", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 14 ],
													"destination" : [ "obj-12", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-12", 0 ],
													"destination" : [ "obj-30", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 15 ],
													"destination" : [ "obj-11", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-11", 0 ],
													"destination" : [ "obj-29", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
 ]
									}
,
									"saved_object_attributes" : 									{
										"default_fontname" : "Arial",
										"fontname" : "Arial",
										"default_fontsize" : 9.0,
										"globalpatchername" : "",
										"fontface" : 0,
										"fontsize" : 9.0,
										"default_fontface" : 0
									}

								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "p Ogroup",
									"id" : "obj-144",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 84.0, 221.5, 17.0 ],
									"numinlets" : 1,
									"fontsize" : 9.27615,
									"numoutlets" : 16,
									"outlettype" : [ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" ],
									"patcher" : 									{
										"fileversion" : 1,
										"rect" : [ 40.0, 141.0, 672.0, 300.0 ],
										"bglocked" : 1,
										"defrect" : [ 40.0, 141.0, 672.0, 300.0 ],
										"openrect" : [ 0.0, 0.0, 0.0, 0.0 ],
										"openinpresentation" : 0,
										"default_fontsize" : 9.0,
										"default_fontface" : 0,
										"default_fontname" : "Arial",
										"gridonopen" : 0,
										"gridsize" : [ 15.0, 15.0 ],
										"gridsnaponopen" : 0,
										"toolbarvisible" : 1,
										"boxanimatetime" : 200,
										"imprint" : 0,
										"enablehscroll" : 1,
										"enablevscroll" : 1,
										"devicewidth" : 0.0,
										"boxes" : [ 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "pack",
													"id" : "obj-1",
													"fontname" : "Arial",
													"patching_rect" : [ 46.0, 181.0, 33.0, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "- 1",
													"id" : "obj-2",
													"fontname" : "Arial",
													"patching_rect" : [ 46.0, 134.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "+ 1",
													"id" : "obj-3",
													"fontname" : "Arial",
													"patching_rect" : [ 19.0, 68.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "* 16",
													"id" : "obj-6",
													"fontname" : "Arial",
													"patching_rect" : [ 19.0, 46.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-7",
													"fontname" : "Arial",
													"patching_rect" : [ 618.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-8",
													"fontname" : "Arial",
													"patching_rect" : [ 580.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-9",
													"fontname" : "Arial",
													"patching_rect" : [ 542.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-10",
													"fontname" : "Arial",
													"patching_rect" : [ 504.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-11",
													"fontname" : "Arial",
													"patching_rect" : [ 465.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-12",
													"fontname" : "Arial",
													"patching_rect" : [ 427.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-13",
													"fontname" : "Arial",
													"patching_rect" : [ 389.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-14",
													"fontname" : "Arial",
													"patching_rect" : [ 351.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-15",
													"fontname" : "Arial",
													"patching_rect" : [ 313.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-16",
													"fontname" : "Arial",
													"patching_rect" : [ 275.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-17",
													"fontname" : "Arial",
													"patching_rect" : [ 237.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-18",
													"fontname" : "Arial",
													"patching_rect" : [ 199.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-19",
													"fontname" : "Arial",
													"patching_rect" : [ 160.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-20",
													"fontname" : "Arial",
													"patching_rect" : [ 122.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-21",
													"fontname" : "Arial",
													"patching_rect" : [ 84.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-22",
													"fontname" : "Arial",
													"patching_rect" : [ 46.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "Uzi 16",
													"id" : "obj-23",
													"fontname" : "Arial",
													"patching_rect" : [ 19.0, 112.0, 46.0, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 3,
													"outlettype" : [ "bang", "bang", "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "+",
													"id" : "obj-24",
													"fontname" : "Arial",
													"patching_rect" : [ 60.0, 159.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "t b i",
													"id" : "obj-25",
													"fontname" : "Arial",
													"patching_rect" : [ 19.0, 90.0, 73.5, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 2,
													"outlettype" : [ "bang", "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "spray 16",
													"id" : "obj-26",
													"fontname" : "Arial",
													"patching_rect" : [ 46.0, 203.0, 591.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 16,
													"outlettype" : [ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "inlet",
													"id" : "obj-27",
													"patching_rect" : [ 19.0, 14.0, 17.0, 17.0 ],
													"numinlets" : 0,
													"numoutlets" : 1,
													"outlettype" : [ "int" ],
													"comment" : "1-16"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-28",
													"patching_rect" : [ 618.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "16"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-29",
													"patching_rect" : [ 580.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "15"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-30",
													"patching_rect" : [ 542.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "14"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-31",
													"patching_rect" : [ 504.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "13"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-32",
													"patching_rect" : [ 465.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "12"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-33",
													"patching_rect" : [ 427.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "11"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-34",
													"patching_rect" : [ 389.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "10"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-35",
													"patching_rect" : [ 351.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "9"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-36",
													"patching_rect" : [ 313.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "8"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-37",
													"patching_rect" : [ 275.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "7"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-38",
													"patching_rect" : [ 237.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "6"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-39",
													"patching_rect" : [ 199.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "5"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-40",
													"patching_rect" : [ 160.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "4"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-41",
													"patching_rect" : [ 122.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "3"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-42",
													"patching_rect" : [ 84.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "2"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-43",
													"patching_rect" : [ 46.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "1"
												}

											}
 ],
										"lines" : [ 											{
												"patchline" : 												{
													"source" : [ "obj-27", 0 ],
													"destination" : [ "obj-6", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-6", 0 ],
													"destination" : [ "obj-3", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-3", 0 ],
													"destination" : [ "obj-25", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-25", 0 ],
													"destination" : [ "obj-23", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-23", 2 ],
													"destination" : [ "obj-2", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-2", 0 ],
													"destination" : [ "obj-1", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-1", 0 ],
													"destination" : [ "obj-26", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 0 ],
													"destination" : [ "obj-22", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-22", 0 ],
													"destination" : [ "obj-43", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-2", 0 ],
													"destination" : [ "obj-24", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-24", 0 ],
													"destination" : [ "obj-1", 1 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-25", 1 ],
													"destination" : [ "obj-24", 1 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 1 ],
													"destination" : [ "obj-21", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-21", 0 ],
													"destination" : [ "obj-42", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 2 ],
													"destination" : [ "obj-20", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-20", 0 ],
													"destination" : [ "obj-41", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 3 ],
													"destination" : [ "obj-19", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-19", 0 ],
													"destination" : [ "obj-40", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 4 ],
													"destination" : [ "obj-18", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-18", 0 ],
													"destination" : [ "obj-39", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 5 ],
													"destination" : [ "obj-17", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-17", 0 ],
													"destination" : [ "obj-38", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 6 ],
													"destination" : [ "obj-16", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-16", 0 ],
													"destination" : [ "obj-37", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 7 ],
													"destination" : [ "obj-15", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-15", 0 ],
													"destination" : [ "obj-36", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 8 ],
													"destination" : [ "obj-14", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-14", 0 ],
													"destination" : [ "obj-35", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 9 ],
													"destination" : [ "obj-13", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-13", 0 ],
													"destination" : [ "obj-34", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 10 ],
													"destination" : [ "obj-12", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-12", 0 ],
													"destination" : [ "obj-33", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 11 ],
													"destination" : [ "obj-11", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-11", 0 ],
													"destination" : [ "obj-32", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 12 ],
													"destination" : [ "obj-10", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-10", 0 ],
													"destination" : [ "obj-31", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 13 ],
													"destination" : [ "obj-9", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-9", 0 ],
													"destination" : [ "obj-30", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 14 ],
													"destination" : [ "obj-8", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-8", 0 ],
													"destination" : [ "obj-29", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 15 ],
													"destination" : [ "obj-7", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-7", 0 ],
													"destination" : [ "obj-28", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
 ]
									}
,
									"saved_object_attributes" : 									{
										"default_fontname" : "Arial",
										"fontname" : "Arial",
										"default_fontsize" : 9.0,
										"globalpatchername" : "",
										"fontface" : 0,
										"fontsize" : 9.0,
										"default_fontface" : 0
									}

								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "Chan. Group",
									"id" : "obj-145",
									"presentation_rect" : [ 115.0, 24.0, 67.0, 17.0 ],
									"fontname" : "Arial Bold",
									"patching_rect" : [ 581.0, 31.0, 67.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 0,
									"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-146",
									"presentation_rect" : [ 118.0, 40.0, 58.939552, 17.0 ],
									"fontname" : "Arial Bold",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 525.0, 30.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "1-16", ",", "17-32", ",", "33-48", ",", "49-64", ",", "65-80", ",", "81-96", ",", "97-112", ",", "113-128", ",", "129-144", ",", "145-160", ",", "161-176", ",", "177-192", ",", "193-208", ",", "209-224", ",", "225-240", ",", "241-256", ",", "257-272", ",", "273-288", ",", "289-304", ",", "305-320", ",", "321-336", ",", "337-352", ",", "353-368", ",", "369-384", ",", "385-400", ",", "401-416", ",", "417-432", ",", "433-448", ",", "449-464", ",", "465-480", ",", "481-496", ",", "497-512" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 16",
									"id" : "obj-147",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 411.0, 87.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 15",
									"id" : "obj-148",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 393.0, 87.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 14",
									"id" : "obj-149",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 375.0, 87.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 13",
									"id" : "obj-150",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 357.0, 87.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 12",
									"id" : "obj-151",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 339.0, 87.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 11",
									"id" : "obj-152",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 321.0, 87.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 10",
									"id" : "obj-153",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 303.0, 87.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 9",
									"id" : "obj-154",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 285.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 8",
									"id" : "obj-155",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 267.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 7",
									"id" : "obj-156",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 249.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 6",
									"id" : "obj-157",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 231.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 5",
									"id" : "obj-158",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 213.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 4",
									"id" : "obj-159",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 195.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 3",
									"id" : "obj-160",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 177.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 2",
									"id" : "obj-161",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 159.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus output 1",
									"id" : "obj-162",
									"fontname" : "Arial",
									"patching_rect" : [ 754.0, 141.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-163",
									"presentation_rect" : [ 117.0, 317.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 411.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-164",
									"presentation_rect" : [ 117.0, 300.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 393.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-165",
									"presentation_rect" : [ 117.0, 283.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 375.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-166",
									"presentation_rect" : [ 117.0, 266.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 357.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-167",
									"presentation_rect" : [ 117.0, 249.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 339.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-168",
									"presentation_rect" : [ 117.0, 232.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 321.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-169",
									"presentation_rect" : [ 117.0, 215.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 303.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-170",
									"presentation_rect" : [ 117.0, 198.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 285.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-171",
									"presentation_rect" : [ 117.0, 181.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 267.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-172",
									"presentation_rect" : [ 117.0, 164.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 249.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-173",
									"presentation_rect" : [ 117.0, 147.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 231.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-174",
									"presentation_rect" : [ 117.0, 130.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 213.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-175",
									"presentation_rect" : [ 117.0, 113.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 195.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-176",
									"presentation_rect" : [ 117.0, 96.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 177.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-177",
									"presentation_rect" : [ 117.0, 79.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 159.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-178",
									"presentation_rect" : [ 117.0, 62.0, 62.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 701.0, 141.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "Output Map.",
									"id" : "obj-179",
									"presentation_rect" : [ 105.0, 3.0, 78.0, 20.0 ],
									"fontname" : "Arial Bold Italic",
									"patching_rect" : [ 652.0, 30.0, 78.0, 20.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 11.595187,
									"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
									"numoutlets" : 0,
									"frgb" : [ 1.0, 1.0, 1.0, 1.0 ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-3",
									"presentation_rect" : [ 5.0, 198.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 285.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 9,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-4",
									"presentation_rect" : [ 5.0, 181.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 267.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 8,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-5",
									"presentation_rect" : [ 5.0, 164.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 249.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 7,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-6",
									"presentation_rect" : [ 5.0, 147.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 231.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 6,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-7",
									"presentation_rect" : [ 5.0, 130.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 213.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 5,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-8",
									"presentation_rect" : [ 5.0, 113.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 195.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 4,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-10",
									"presentation_rect" : [ 5.0, 96.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 177.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 3,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-11",
									"presentation_rect" : [ 5.0, 215.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 303.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 10,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-12",
									"presentation_rect" : [ 5.0, 232.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 321.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 11,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-13",
									"presentation_rect" : [ 5.0, 249.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 339.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 12,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-14",
									"presentation_rect" : [ 5.0, 266.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 357.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 13,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-15",
									"presentation_rect" : [ 5.0, 283.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 375.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 14,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-16",
									"presentation_rect" : [ 5.0, 300.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 393.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 15,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-17",
									"presentation_rect" : [ 5.0, 317.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 411.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 16,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-18",
									"presentation_rect" : [ 5.0, 79.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 159.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 2,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"id" : "obj-19",
									"presentation_rect" : [ 5.0, 62.0, 21.0, 17.0 ],
									"fontname" : "Arial Bold",
									"types" : [  ],
									"patching_rect" : [ 30.0, 141.0, 28.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 0.858824, 0.858824, 0.858824, 0.0 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"align" : 1,
									"menumode" : 2,
									"items" : 1,
									"framecolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "p Igrouplabel",
									"id" : "obj-20",
									"fontname" : "Arial",
									"patching_rect" : [ 30.0, 84.0, 221.5, 17.0 ],
									"numinlets" : 1,
									"fontsize" : 9.27615,
									"numoutlets" : 16,
									"outlettype" : [ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" ],
									"patcher" : 									{
										"fileversion" : 1,
										"rect" : [ 228.0, 171.0, 713.0, 321.0 ],
										"bglocked" : 1,
										"defrect" : [ 228.0, 171.0, 713.0, 321.0 ],
										"openrect" : [ 0.0, 0.0, 0.0, 0.0 ],
										"openinpresentation" : 0,
										"default_fontsize" : 9.0,
										"default_fontface" : 0,
										"default_fontname" : "Arial",
										"gridonopen" : 0,
										"gridsize" : [ 15.0, 15.0 ],
										"gridsnaponopen" : 0,
										"toolbarvisible" : 1,
										"boxanimatetime" : 200,
										"imprint" : 0,
										"enablehscroll" : 1,
										"enablevscroll" : 1,
										"devicewidth" : 0.0,
										"boxes" : [ 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "pack",
													"id" : "obj-1",
													"fontname" : "Arial",
													"patching_rect" : [ 55.0, 177.0, 33.0, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "- 1",
													"id" : "obj-2",
													"fontname" : "Arial",
													"patching_rect" : [ 55.0, 129.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "+ 1",
													"id" : "obj-3",
													"fontname" : "Arial",
													"patching_rect" : [ 27.0, 63.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "* 16",
													"id" : "obj-6",
													"fontname" : "Arial",
													"patching_rect" : [ 27.0, 42.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "Uzi 16",
													"id" : "obj-7",
													"fontname" : "Arial",
													"patching_rect" : [ 27.0, 107.0, 47.0, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 3,
													"outlettype" : [ "bang", "bang", "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "+",
													"id" : "obj-8",
													"fontname" : "Arial",
													"patching_rect" : [ 69.0, 155.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "t b i",
													"id" : "obj-9",
													"fontname" : "Arial",
													"patching_rect" : [ 27.0, 85.0, 74.5, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 2,
													"outlettype" : [ "bang", "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-11",
													"fontname" : "Arial",
													"patching_rect" : [ 624.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-12",
													"fontname" : "Arial",
													"patching_rect" : [ 586.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-13",
													"fontname" : "Arial",
													"patching_rect" : [ 548.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-14",
													"fontname" : "Arial",
													"patching_rect" : [ 510.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-15",
													"fontname" : "Arial",
													"patching_rect" : [ 472.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-16",
													"fontname" : "Arial",
													"patching_rect" : [ 434.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-17",
													"fontname" : "Arial",
													"patching_rect" : [ 396.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-18",
													"fontname" : "Arial",
													"patching_rect" : [ 358.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-19",
													"fontname" : "Arial",
													"patching_rect" : [ 321.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-20",
													"fontname" : "Arial",
													"patching_rect" : [ 283.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-21",
													"fontname" : "Arial",
													"patching_rect" : [ 245.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-22",
													"fontname" : "Arial",
													"patching_rect" : [ 207.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-23",
													"fontname" : "Arial",
													"patching_rect" : [ 169.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-24",
													"fontname" : "Arial",
													"patching_rect" : [ 131.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-25",
													"fontname" : "Arial",
													"patching_rect" : [ 93.0, 246.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "setitem 0 $1",
													"id" : "obj-26",
													"fontname" : "Arial",
													"patching_rect" : [ 55.0, 221.0, 73.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "spray 16",
													"id" : "obj-27",
													"fontname" : "Arial",
													"patching_rect" : [ 55.0, 199.0, 588.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 16,
													"outlettype" : [ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "inlet",
													"id" : "obj-28",
													"patching_rect" : [ 27.0, 15.0, 17.0, 17.0 ],
													"numinlets" : 0,
													"numoutlets" : 1,
													"outlettype" : [ "int" ],
													"comment" : "1-16"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-29",
													"patching_rect" : [ 624.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "16"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-30",
													"patching_rect" : [ 586.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "15"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-31",
													"patching_rect" : [ 548.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "14"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-32",
													"patching_rect" : [ 510.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "13"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-33",
													"patching_rect" : [ 472.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "12"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-34",
													"patching_rect" : [ 434.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "11"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-35",
													"patching_rect" : [ 396.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "10"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-36",
													"patching_rect" : [ 358.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "9"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-37",
													"patching_rect" : [ 321.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "8"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-38",
													"patching_rect" : [ 283.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "7"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-39",
													"patching_rect" : [ 245.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "6"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-40",
													"patching_rect" : [ 207.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "5"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-41",
													"patching_rect" : [ 169.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "4"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-42",
													"patching_rect" : [ 131.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "3"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-43",
													"patching_rect" : [ 93.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "2"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-44",
													"patching_rect" : [ 55.0, 277.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "1"
												}

											}
 ],
										"lines" : [ 											{
												"patchline" : 												{
													"source" : [ "obj-28", 0 ],
													"destination" : [ "obj-6", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-6", 0 ],
													"destination" : [ "obj-3", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-3", 0 ],
													"destination" : [ "obj-9", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-9", 0 ],
													"destination" : [ "obj-7", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-7", 2 ],
													"destination" : [ "obj-2", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-2", 0 ],
													"destination" : [ "obj-1", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-1", 0 ],
													"destination" : [ "obj-27", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 0 ],
													"destination" : [ "obj-26", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 0 ],
													"destination" : [ "obj-44", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-2", 0 ],
													"destination" : [ "obj-8", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-8", 0 ],
													"destination" : [ "obj-1", 1 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-9", 1 ],
													"destination" : [ "obj-8", 1 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 1 ],
													"destination" : [ "obj-25", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-25", 0 ],
													"destination" : [ "obj-43", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 2 ],
													"destination" : [ "obj-24", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-24", 0 ],
													"destination" : [ "obj-42", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 3 ],
													"destination" : [ "obj-23", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-23", 0 ],
													"destination" : [ "obj-41", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 4 ],
													"destination" : [ "obj-22", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-22", 0 ],
													"destination" : [ "obj-40", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 5 ],
													"destination" : [ "obj-21", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-21", 0 ],
													"destination" : [ "obj-39", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 6 ],
													"destination" : [ "obj-20", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-20", 0 ],
													"destination" : [ "obj-38", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 7 ],
													"destination" : [ "obj-19", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-19", 0 ],
													"destination" : [ "obj-37", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 8 ],
													"destination" : [ "obj-18", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-18", 0 ],
													"destination" : [ "obj-36", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 9 ],
													"destination" : [ "obj-17", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-17", 0 ],
													"destination" : [ "obj-35", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 10 ],
													"destination" : [ "obj-16", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-16", 0 ],
													"destination" : [ "obj-34", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 11 ],
													"destination" : [ "obj-15", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-15", 0 ],
													"destination" : [ "obj-33", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 12 ],
													"destination" : [ "obj-14", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-14", 0 ],
													"destination" : [ "obj-32", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 13 ],
													"destination" : [ "obj-13", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-13", 0 ],
													"destination" : [ "obj-31", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 14 ],
													"destination" : [ "obj-12", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-12", 0 ],
													"destination" : [ "obj-30", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-27", 15 ],
													"destination" : [ "obj-11", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-11", 0 ],
													"destination" : [ "obj-29", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
 ]
									}
,
									"saved_object_attributes" : 									{
										"default_fontname" : "Arial",
										"fontname" : "Arial",
										"default_fontsize" : 9.0,
										"globalpatchername" : "",
										"fontface" : 0,
										"fontsize" : 9.0,
										"default_fontface" : 0
									}

								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "p Igroup",
									"id" : "obj-36",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 84.0, 221.5, 17.0 ],
									"numinlets" : 1,
									"fontsize" : 9.27615,
									"numoutlets" : 16,
									"outlettype" : [ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" ],
									"patcher" : 									{
										"fileversion" : 1,
										"rect" : [ 40.0, 141.0, 672.0, 300.0 ],
										"bglocked" : 1,
										"defrect" : [ 40.0, 141.0, 672.0, 300.0 ],
										"openrect" : [ 0.0, 0.0, 0.0, 0.0 ],
										"openinpresentation" : 0,
										"default_fontsize" : 9.0,
										"default_fontface" : 0,
										"default_fontname" : "Arial",
										"gridonopen" : 0,
										"gridsize" : [ 15.0, 15.0 ],
										"gridsnaponopen" : 0,
										"toolbarvisible" : 1,
										"boxanimatetime" : 200,
										"imprint" : 0,
										"enablehscroll" : 1,
										"enablevscroll" : 1,
										"devicewidth" : 0.0,
										"boxes" : [ 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "pack",
													"id" : "obj-1",
													"fontname" : "Arial",
													"patching_rect" : [ 46.0, 181.0, 33.0, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "- 1",
													"id" : "obj-2",
													"fontname" : "Arial",
													"patching_rect" : [ 46.0, 134.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "+ 1",
													"id" : "obj-3",
													"fontname" : "Arial",
													"patching_rect" : [ 19.0, 68.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "* 16",
													"id" : "obj-6",
													"fontname" : "Arial",
													"patching_rect" : [ 19.0, 46.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-7",
													"fontname" : "Arial",
													"patching_rect" : [ 618.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-8",
													"fontname" : "Arial",
													"patching_rect" : [ 580.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-9",
													"fontname" : "Arial",
													"patching_rect" : [ 542.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-10",
													"fontname" : "Arial",
													"patching_rect" : [ 504.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-11",
													"fontname" : "Arial",
													"patching_rect" : [ 465.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-12",
													"fontname" : "Arial",
													"patching_rect" : [ 427.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-13",
													"fontname" : "Arial",
													"patching_rect" : [ 389.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-14",
													"fontname" : "Arial",
													"patching_rect" : [ 351.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-15",
													"fontname" : "Arial",
													"patching_rect" : [ 313.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-16",
													"fontname" : "Arial",
													"patching_rect" : [ 275.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-17",
													"fontname" : "Arial",
													"patching_rect" : [ 237.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-18",
													"fontname" : "Arial",
													"patching_rect" : [ 199.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-19",
													"fontname" : "Arial",
													"patching_rect" : [ 160.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-20",
													"fontname" : "Arial",
													"patching_rect" : [ 122.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-21",
													"fontname" : "Arial",
													"patching_rect" : [ 84.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "message",
													"text" : "set $1",
													"id" : "obj-22",
													"fontname" : "Arial",
													"patching_rect" : [ 46.0, 225.0, 37.0, 16.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "Uzi 16",
													"id" : "obj-23",
													"fontname" : "Arial",
													"patching_rect" : [ 19.0, 112.0, 46.0, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 3,
													"outlettype" : [ "bang", "bang", "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "+",
													"id" : "obj-24",
													"fontname" : "Arial",
													"patching_rect" : [ 60.0, 159.0, 32.5, 18.0 ],
													"numinlets" : 2,
													"fontsize" : 10.435669,
													"numoutlets" : 1,
													"outlettype" : [ "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "t b i",
													"id" : "obj-25",
													"fontname" : "Arial",
													"patching_rect" : [ 19.0, 90.0, 73.5, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 2,
													"outlettype" : [ "bang", "int" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "newobj",
													"text" : "spray 16",
													"id" : "obj-26",
													"fontname" : "Arial",
													"patching_rect" : [ 46.0, 203.0, 591.0, 18.0 ],
													"numinlets" : 1,
													"fontsize" : 10.435669,
													"numoutlets" : 16,
													"outlettype" : [ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" ]
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "inlet",
													"id" : "obj-27",
													"patching_rect" : [ 19.0, 14.0, 17.0, 17.0 ],
													"numinlets" : 0,
													"numoutlets" : 1,
													"outlettype" : [ "int" ],
													"comment" : "1-16"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-28",
													"patching_rect" : [ 618.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "16"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-29",
													"patching_rect" : [ 580.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "15"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-30",
													"patching_rect" : [ 542.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "14"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-31",
													"patching_rect" : [ 504.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "13"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-32",
													"patching_rect" : [ 465.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "12"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-33",
													"patching_rect" : [ 427.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "11"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-34",
													"patching_rect" : [ 389.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "10"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-35",
													"patching_rect" : [ 351.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "9"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-36",
													"patching_rect" : [ 313.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "8"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-37",
													"patching_rect" : [ 275.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "7"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-38",
													"patching_rect" : [ 237.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "6"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-39",
													"patching_rect" : [ 199.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "5"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-40",
													"patching_rect" : [ 160.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "4"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-41",
													"patching_rect" : [ 122.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "3"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-42",
													"patching_rect" : [ 84.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "2"
												}

											}
, 											{
												"box" : 												{
													"maxclass" : "outlet",
													"id" : "obj-43",
													"patching_rect" : [ 46.0, 253.0, 17.0, 17.0 ],
													"numinlets" : 1,
													"numoutlets" : 0,
													"comment" : "1"
												}

											}
 ],
										"lines" : [ 											{
												"patchline" : 												{
													"source" : [ "obj-27", 0 ],
													"destination" : [ "obj-6", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-6", 0 ],
													"destination" : [ "obj-3", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-3", 0 ],
													"destination" : [ "obj-25", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-25", 0 ],
													"destination" : [ "obj-23", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-23", 2 ],
													"destination" : [ "obj-2", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-2", 0 ],
													"destination" : [ "obj-1", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-1", 0 ],
													"destination" : [ "obj-26", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 0 ],
													"destination" : [ "obj-22", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-22", 0 ],
													"destination" : [ "obj-43", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-2", 0 ],
													"destination" : [ "obj-24", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-24", 0 ],
													"destination" : [ "obj-1", 1 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-25", 1 ],
													"destination" : [ "obj-24", 1 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 1 ],
													"destination" : [ "obj-21", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-21", 0 ],
													"destination" : [ "obj-42", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 2 ],
													"destination" : [ "obj-20", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-20", 0 ],
													"destination" : [ "obj-41", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 3 ],
													"destination" : [ "obj-19", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-19", 0 ],
													"destination" : [ "obj-40", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 4 ],
													"destination" : [ "obj-18", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-18", 0 ],
													"destination" : [ "obj-39", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 5 ],
													"destination" : [ "obj-17", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-17", 0 ],
													"destination" : [ "obj-38", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 6 ],
													"destination" : [ "obj-16", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-16", 0 ],
													"destination" : [ "obj-37", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 7 ],
													"destination" : [ "obj-15", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-15", 0 ],
													"destination" : [ "obj-36", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 8 ],
													"destination" : [ "obj-14", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-14", 0 ],
													"destination" : [ "obj-35", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 9 ],
													"destination" : [ "obj-13", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-13", 0 ],
													"destination" : [ "obj-34", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 10 ],
													"destination" : [ "obj-12", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-12", 0 ],
													"destination" : [ "obj-33", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 11 ],
													"destination" : [ "obj-11", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-11", 0 ],
													"destination" : [ "obj-32", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 12 ],
													"destination" : [ "obj-10", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-10", 0 ],
													"destination" : [ "obj-31", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 13 ],
													"destination" : [ "obj-9", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-9", 0 ],
													"destination" : [ "obj-30", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 14 ],
													"destination" : [ "obj-8", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-8", 0 ],
													"destination" : [ "obj-29", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-26", 15 ],
													"destination" : [ "obj-7", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
, 											{
												"patchline" : 												{
													"source" : [ "obj-7", 0 ],
													"destination" : [ "obj-28", 0 ],
													"hidden" : 0,
													"midpoints" : [  ]
												}

											}
 ]
									}
,
									"saved_object_attributes" : 									{
										"default_fontname" : "Arial",
										"fontname" : "Arial",
										"default_fontsize" : 9.0,
										"globalpatchername" : "",
										"fontface" : 0,
										"fontsize" : 9.0,
										"default_fontface" : 0
									}

								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "Chan. Group",
									"id" : "obj-38",
									"presentation_rect" : [ 24.0, 24.0, 67.0, 17.0 ],
									"fontname" : "Arial Bold",
									"patching_rect" : [ 315.0, 31.0, 67.0, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 0,
									"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-39",
									"presentation_rect" : [ 28.0, 40.0, 58.939552, 17.0 ],
									"fontname" : "Arial Bold",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 259.0, 30.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "1-16", ",", "17-32", ",", "33-48", ",", "49-64", ",", "65-80", ",", "81-96", ",", "97-112", ",", "113-128", ",", "129-144", ",", "145-160", ",", "161-176", ",", "177-192", ",", "193-208", ",", "209-224", ",", "225-240", ",", "241-256", ",", "257-272", ",", "273-288", ",", "289-304", ",", "305-320", ",", "321-336", ",", "337-352", ",", "353-368", ",", "369-384", ",", "385-400", ",", "401-416", ",", "417-432", ",", "433-448", ",", "449-464", ",", "465-480", ",", "481-496", ",", "497-512" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 16",
									"id" : "obj-60",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 411.0, 87.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 15",
									"id" : "obj-61",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 393.0, 87.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 14",
									"id" : "obj-62",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 375.0, 87.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 13",
									"id" : "obj-63",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 357.0, 87.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 12",
									"id" : "obj-64",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 339.0, 87.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 11",
									"id" : "obj-65",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 321.0, 87.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 10",
									"id" : "obj-66",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 303.0, 87.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 9",
									"id" : "obj-67",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 285.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 8",
									"id" : "obj-68",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 267.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 7",
									"id" : "obj-69",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 249.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 6",
									"id" : "obj-70",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 231.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 5",
									"id" : "obj-71",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 213.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 4",
									"id" : "obj-72",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 195.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 3",
									"id" : "obj-73",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 177.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 2",
									"id" : "obj-74",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 159.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "adstatus input 1",
									"id" : "obj-75",
									"fontname" : "Arial",
									"patching_rect" : [ 259.0, 141.0, 82.0, 17.0 ],
									"numinlets" : 2,
									"fontsize" : 9.27615,
									"numoutlets" : 2,
									"outlettype" : [ "", "int" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-93",
									"presentation_rect" : [ 27.0, 317.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 411.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-94",
									"presentation_rect" : [ 27.0, 300.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 393.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-95",
									"presentation_rect" : [ 27.0, 283.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 375.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-96",
									"presentation_rect" : [ 27.0, 266.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 357.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-97",
									"presentation_rect" : [ 27.0, 249.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 339.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-98",
									"presentation_rect" : [ 27.0, 232.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 321.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-99",
									"presentation_rect" : [ 27.0, 215.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 303.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-100",
									"presentation_rect" : [ 27.0, 198.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 285.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-101",
									"presentation_rect" : [ 27.0, 181.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 267.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-102",
									"presentation_rect" : [ 27.0, 164.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 249.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-103",
									"presentation_rect" : [ 27.0, 147.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 231.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-104",
									"presentation_rect" : [ 27.0, 130.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 213.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-105",
									"presentation_rect" : [ 27.0, 113.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 195.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-106",
									"presentation_rect" : [ 27.0, 96.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 177.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-107",
									"presentation_rect" : [ 27.0, 79.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 159.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "umenu",
									"hint" : "",
									"prototypename" : "Arial9",
									"pattrmode" : 1,
									"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
									"id" : "obj-108",
									"presentation_rect" : [ 27.0, 62.0, 61.939552, 17.0 ],
									"fontname" : "Arial",
									"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
									"arrowlink" : 0,
									"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
									"prefix_mode" : 2,
									"types" : [  ],
									"patching_rect" : [ 206.0, 141.0, 50.939552, 17.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 9.27615,
									"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
									"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
									"numoutlets" : 3,
									"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
									"arrowframe" : 0,
									"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
									"outlettype" : [ "int", "", "" ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "comment",
									"text" : "Input Map.",
									"id" : "obj-109",
									"presentation_rect" : [ 19.0, 3.0, 68.0, 20.0 ],
									"fontname" : "Arial Bold Italic",
									"patching_rect" : [ 386.0, 30.0, 68.0, 20.0 ],
									"numinlets" : 1,
									"presentation" : 1,
									"fontsize" : 11.595187,
									"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
									"numoutlets" : 0,
									"frgb" : [ 1.0, 1.0, 1.0, 1.0 ]
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "inlet",
									"prototypename" : "Arial9",
									"id" : "obj-9",
									"patching_rect" : [ 189.0, 25.0, 18.0, 18.0 ],
									"numinlets" : 0,
									"numoutlets" : 1,
									"outlettype" : [ "" ],
									"comment" : ""
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "panel",
									"hint" : "",
									"prototypename" : "Arial9",
									"id" : "obj-1",
									"presentation_rect" : [ 99.0, 23.0, 87.0, 319.0 ],
									"background" : 1,
									"bordercolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
									"patching_rect" : [ 458.0, 55.0, 20.0, 20.0 ],
									"numinlets" : 1,
									"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
									"presentation" : 1,
									"bgcolor" : [ 0.658824, 0.658824, 0.74902, 1.0 ],
									"rounded" : 16,
									"numoutlets" : 0,
									"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
									"shadow" : -1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "panel",
									"hint" : "",
									"prototypename" : "Arial9",
									"id" : "obj-235",
									"background" : 1,
									"bordercolor" : [ 0.670588, 0.670588, 0.74902, 1.0 ],
									"patching_rect" : [ 15.0, 15.0, 480.0, 435.0 ],
									"numinlets" : 1,
									"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
									"bgcolor" : [ 0.741176, 0.741176, 0.803922, 0.0 ],
									"rounded" : 20,
									"numoutlets" : 0,
									"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
									"border" : 1,
									"shadow" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "panel",
									"hint" : "",
									"prototypename" : "Arial9",
									"id" : "obj-212",
									"presentation_rect" : [ 8.0, 23.0, 87.0, 319.0 ],
									"background" : 1,
									"bordercolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
									"patching_rect" : [ 735.0, 32.0, 20.0, 20.0 ],
									"numinlets" : 1,
									"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
									"presentation" : 1,
									"bgcolor" : [ 0.658824, 0.658824, 0.74902, 1.0 ],
									"rounded" : 16,
									"numoutlets" : 0,
									"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
									"shadow" : -1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "panel",
									"hint" : "",
									"prototypename" : "Arial9",
									"id" : "obj-24",
									"presentation_rect" : [ -0.547028, 0.0, 195.0, 351.0 ],
									"background" : 1,
									"bordercolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
									"patching_rect" : [ 458.0, 30.0, 20.0, 20.0 ],
									"numinlets" : 1,
									"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
									"presentation" : 1,
									"bgcolor" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
									"rounded" : 0,
									"numoutlets" : 0,
									"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
									"shadow" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "panel",
									"hint" : "",
									"prototypename" : "Arial9",
									"id" : "obj-236",
									"background" : 1,
									"bordercolor" : [ 0.670588, 0.670588, 0.74902, 1.0 ],
									"patching_rect" : [ 510.0, 15.0, 480.0, 435.0 ],
									"numinlets" : 1,
									"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
									"bgcolor" : [ 0.741176, 0.741176, 0.803922, 0.0 ],
									"rounded" : 20,
									"numoutlets" : 0,
									"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
									"border" : 1,
									"shadow" : 1
								}

							}
 ],
						"lines" : [ 							{
								"patchline" : 								{
									"source" : [ "obj-237", 0 ],
									"destination" : [ "obj-239", 1 ],
									"hidden" : 1,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-84", 0 ],
									"destination" : [ "obj-239", 0 ],
									"hidden" : 1,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-239", 0 ],
									"destination" : [ "obj-238", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-147", 0 ],
									"destination" : [ "obj-163", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-163", 0 ],
									"destination" : [ "obj-147", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-148", 0 ],
									"destination" : [ "obj-164", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-164", 0 ],
									"destination" : [ "obj-148", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-149", 0 ],
									"destination" : [ "obj-165", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-165", 0 ],
									"destination" : [ "obj-149", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-150", 0 ],
									"destination" : [ "obj-166", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-166", 0 ],
									"destination" : [ "obj-150", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-151", 0 ],
									"destination" : [ "obj-167", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-167", 0 ],
									"destination" : [ "obj-151", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-152", 0 ],
									"destination" : [ "obj-168", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-168", 0 ],
									"destination" : [ "obj-152", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-153", 0 ],
									"destination" : [ "obj-169", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-169", 0 ],
									"destination" : [ "obj-153", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-154", 0 ],
									"destination" : [ "obj-170", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-170", 0 ],
									"destination" : [ "obj-154", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-155", 0 ],
									"destination" : [ "obj-171", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-171", 0 ],
									"destination" : [ "obj-155", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-156", 0 ],
									"destination" : [ "obj-172", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-172", 0 ],
									"destination" : [ "obj-156", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-157", 0 ],
									"destination" : [ "obj-173", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-173", 0 ],
									"destination" : [ "obj-157", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-158", 0 ],
									"destination" : [ "obj-174", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-174", 0 ],
									"destination" : [ "obj-158", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-159", 0 ],
									"destination" : [ "obj-175", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-175", 0 ],
									"destination" : [ "obj-159", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-160", 0 ],
									"destination" : [ "obj-176", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-176", 0 ],
									"destination" : [ "obj-160", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-161", 0 ],
									"destination" : [ "obj-177", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-177", 0 ],
									"destination" : [ "obj-161", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-162", 0 ],
									"destination" : [ "obj-178", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-178", 0 ],
									"destination" : [ "obj-162", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 0 ],
									"destination" : [ "obj-162", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 1 ],
									"destination" : [ "obj-161", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 2 ],
									"destination" : [ "obj-160", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 3 ],
									"destination" : [ "obj-159", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 4 ],
									"destination" : [ "obj-158", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 5 ],
									"destination" : [ "obj-157", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 6 ],
									"destination" : [ "obj-156", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 7 ],
									"destination" : [ "obj-155", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 8 ],
									"destination" : [ "obj-154", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 9 ],
									"destination" : [ "obj-153", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 10 ],
									"destination" : [ "obj-152", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 11 ],
									"destination" : [ "obj-151", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 12 ],
									"destination" : [ "obj-150", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 13 ],
									"destination" : [ "obj-149", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 14 ],
									"destination" : [ "obj-148", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-144", 15 ],
									"destination" : [ "obj-147", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 0 ],
									"destination" : [ "obj-142", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 1 ],
									"destination" : [ "obj-141", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 2 ],
									"destination" : [ "obj-133", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 3 ],
									"destination" : [ "obj-132", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 4 ],
									"destination" : [ "obj-131", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 5 ],
									"destination" : [ "obj-130", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 6 ],
									"destination" : [ "obj-129", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 7 ],
									"destination" : [ "obj-128", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 8 ],
									"destination" : [ "obj-127", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 9 ],
									"destination" : [ "obj-134", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 10 ],
									"destination" : [ "obj-135", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 11 ],
									"destination" : [ "obj-136", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 12 ],
									"destination" : [ "obj-137", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 13 ],
									"destination" : [ "obj-138", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 14 ],
									"destination" : [ "obj-139", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-143", 15 ],
									"destination" : [ "obj-140", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-146", 0 ],
									"destination" : [ "obj-143", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-146", 0 ],
									"destination" : [ "obj-144", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-60", 0 ],
									"destination" : [ "obj-93", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-93", 0 ],
									"destination" : [ "obj-60", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-61", 0 ],
									"destination" : [ "obj-94", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-94", 0 ],
									"destination" : [ "obj-61", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-62", 0 ],
									"destination" : [ "obj-95", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-95", 0 ],
									"destination" : [ "obj-62", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-63", 0 ],
									"destination" : [ "obj-96", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-96", 0 ],
									"destination" : [ "obj-63", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-64", 0 ],
									"destination" : [ "obj-97", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-97", 0 ],
									"destination" : [ "obj-64", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-65", 0 ],
									"destination" : [ "obj-98", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-98", 0 ],
									"destination" : [ "obj-65", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-66", 0 ],
									"destination" : [ "obj-99", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-99", 0 ],
									"destination" : [ "obj-66", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-67", 0 ],
									"destination" : [ "obj-100", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-100", 0 ],
									"destination" : [ "obj-67", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-68", 0 ],
									"destination" : [ "obj-101", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-101", 0 ],
									"destination" : [ "obj-68", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-69", 0 ],
									"destination" : [ "obj-102", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-102", 0 ],
									"destination" : [ "obj-69", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-70", 0 ],
									"destination" : [ "obj-103", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-103", 0 ],
									"destination" : [ "obj-70", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-71", 0 ],
									"destination" : [ "obj-104", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-104", 0 ],
									"destination" : [ "obj-71", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-72", 0 ],
									"destination" : [ "obj-105", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-105", 0 ],
									"destination" : [ "obj-72", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-73", 0 ],
									"destination" : [ "obj-106", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-106", 0 ],
									"destination" : [ "obj-73", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-74", 0 ],
									"destination" : [ "obj-107", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-107", 0 ],
									"destination" : [ "obj-74", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-75", 0 ],
									"destination" : [ "obj-108", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-108", 0 ],
									"destination" : [ "obj-75", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 0 ],
									"destination" : [ "obj-75", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 1 ],
									"destination" : [ "obj-74", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 2 ],
									"destination" : [ "obj-73", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 3 ],
									"destination" : [ "obj-72", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 4 ],
									"destination" : [ "obj-71", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 5 ],
									"destination" : [ "obj-70", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 6 ],
									"destination" : [ "obj-69", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 7 ],
									"destination" : [ "obj-68", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 8 ],
									"destination" : [ "obj-67", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 9 ],
									"destination" : [ "obj-66", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 10 ],
									"destination" : [ "obj-65", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 11 ],
									"destination" : [ "obj-64", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 12 ],
									"destination" : [ "obj-63", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 13 ],
									"destination" : [ "obj-62", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 14 ],
									"destination" : [ "obj-61", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-36", 15 ],
									"destination" : [ "obj-60", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 0 ],
									"destination" : [ "obj-19", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 1 ],
									"destination" : [ "obj-18", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 2 ],
									"destination" : [ "obj-10", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 3 ],
									"destination" : [ "obj-8", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 4 ],
									"destination" : [ "obj-7", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 5 ],
									"destination" : [ "obj-6", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 6 ],
									"destination" : [ "obj-5", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 7 ],
									"destination" : [ "obj-4", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 8 ],
									"destination" : [ "obj-3", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 9 ],
									"destination" : [ "obj-11", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 10 ],
									"destination" : [ "obj-12", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 11 ],
									"destination" : [ "obj-13", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 12 ],
									"destination" : [ "obj-14", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 13 ],
									"destination" : [ "obj-15", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 14 ],
									"destination" : [ "obj-16", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 15 ],
									"destination" : [ "obj-17", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-39", 0 ],
									"destination" : [ "obj-20", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-39", 0 ],
									"destination" : [ "obj-36", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
 ]
					}
,
					"saved_object_attributes" : 					{
						"default_fontname" : "Arial",
						"fontname" : "Arial",
						"default_fontsize" : 9.0,
						"globalpatchername" : "",
						"fontface" : 0,
						"fontsize" : 9.0,
						"default_fontface" : 0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "open",
					"id" : "obj-113",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"ignoreclick" : 0,
					"patching_rect" : [ 425.0, 459.0, 34.0, 16.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 0.867, 0.867, 0.867, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 1,
					"bgcolor2" : [ 0.867, 0.867, 0.867, 1.0 ],
					"gradient" : 0,
					"outlettype" : [ "" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "pcontrol",
					"id" : "obj-112",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 425.0, 479.0, 48.0, 18.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 1,
					"outlettype" : [ "" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "textbutton",
					"hint" : "",
					"spacing_y" : 4.0,
					"textoncolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"id" : "obj-83",
					"presentation_rect" : [ 151.0, 353.0, 96.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"texton" : "Button On",
					"outputmode" : 1,
					"active" : 1,
					"fontlink" : 0,
					"textovercolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"bordercolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 425.0, 435.0, 100.0, 20.0 ],
					"numinlets" : 1,
					"bgovercolor" : [ 0.670588, 0.670588, 0.74902, 1.0 ],
					"truncate" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"tosymbol" : 1,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"hidden" : 0,
					"rounded" : 14.0,
					"underline" : 0,
					"borderoncolor" : [ 0.4, 0.4, 0.4, 1.0 ],
					"textcolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
					"numoutlets" : 3,
					"bgoveroncolor" : [ 0.670588, 0.670588, 0.74902, 1.0 ],
					"align" : 1,
					"mode" : 0,
					"textoveroncolor" : [ 0.9, 0.9, 0.9, 1.0 ],
					"border" : 2,
					"spacing_x" : 4.0,
					"outlettype" : [ "", "", "int" ],
					"bgoncolor" : [ 0.368627, 0.360784, 0.501961, 0.74902 ],
					"text" : "I/O Mappings",
					"blinktime" : 150
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "textbutton",
					"hint" : "",
					"spacing_y" : 4.0,
					"textoncolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"id" : "obj-81",
					"presentation_rect" : [ 18.0, 353.0, 111.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"texton" : "Button On",
					"outputmode" : 1,
					"active" : 1,
					"fontlink" : 0,
					"textovercolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"bordercolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 425.0, 375.0, 100.0, 20.0 ],
					"numinlets" : 1,
					"bgovercolor" : [ 0.670588, 0.670588, 0.74902, 1.0 ],
					"truncate" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"tosymbol" : 1,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"hidden" : 0,
					"rounded" : 14.0,
					"underline" : 0,
					"borderoncolor" : [ 0.4, 0.4, 0.4, 1.0 ],
					"textcolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
					"numoutlets" : 3,
					"bgoveroncolor" : [ 0.670588, 0.670588, 0.74902, 1.0 ],
					"align" : 1,
					"mode" : 0,
					"textoveroncolor" : [ 0.9, 0.9, 0.9, 1.0 ],
					"border" : 2,
					"spacing_x" : 4.0,
					"outlettype" : [ "", "", "int" ],
					"bgoncolor" : [ 0.368627, 0.360784, 0.501961, 0.74902 ],
					"text" : "Audio Driver Setup",
					"blinktime" : 150
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Channel 2",
					"id" : "obj-29",
					"presentation_rect" : [ 134.0, 329.0, 55.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 359.0, 450.0, 55.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Channel 1",
					"id" : "obj-75",
					"presentation_rect" : [ 134.0, 310.0, 55.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 239.0, 450.0, 55.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "DSP",
					"id" : "obj-82",
					"presentation_rect" : [ 205.0, 8.0, 40.0, 22.0 ],
					"fontname" : "Arial Bold Italic",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 329.0, 26.0, 39.0, 22.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 13.914225,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 1.0, 1.0, 1.0, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Hz",
					"id" : "obj-49",
					"presentation_rect" : [ 223.0, 156.0, 22.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 234.0, 217.0, 22.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "button",
					"hint" : "",
					"outlinecolor" : [ 0.368627, 0.360784, 0.501961, 0.74902 ],
					"id" : "obj-108",
					"presentation_rect" : [ 226.0, 226.0, 11.0, 11.0 ],
					"background" : 0,
					"fgcolor" : [ 0.368627, 0.360784, 0.501961, 0.74902 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 614.0, 238.0, 19.0, 19.0 ],
					"numinlets" : 1,
					"presentation" : 1,
					"bgcolor" : [ 0.913725, 0.913725, 0.913725, 0.0 ],
					"hidden" : 0,
					"numoutlets" : 1,
					"blinkcolor" : [ 1.0, 0.0, 0.0, 1.0 ],
					"outlettype" : [ "bang" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Overload",
					"id" : "obj-106",
					"presentation_rect" : [ 167.0, 224.0, 53.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 563.0, 240.0, 51.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "slider",
					"hint" : "",
					"id" : "obj-104",
					"floatoutput" : 0,
					"presentation_rect" : [ 146.0, 210.0, 100.620918, 10.0 ],
					"background" : 0,
					"size" : 101.0,
					"bordercolor" : [ 0.694118, 0.694118, 0.694118, 0.0 ],
					"relative" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 455.0, 285.0, 126.0, 10.0 ],
					"numinlets" : 1,
					"mult" : 1.0,
					"presentation" : 1,
					"min" : 0.0,
					"bgcolor" : [ 0.368627, 0.360784, 0.501961, 0.74902 ],
					"knobcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"numoutlets" : 1,
					"orientation" : 0,
					"outlettype" : [ "" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "substitute append text",
					"id" : "obj-101",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 515.0, 52.0, 113.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus optionname 1",
					"id" : "obj-102",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 515.0, 30.0, 116.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "textbutton",
					"hint" : "",
					"spacing_y" : 4.0,
					"textoncolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"id" : "obj-103",
					"presentation_rect" : [ 14.0, 66.0, 91.0, 20.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"texton" : "Button On",
					"outputmode" : 1,
					"active" : 1,
					"fontlink" : 0,
					"textovercolor" : [ 0.101961, 0.101961, 0.101961, 0.0 ],
					"bordercolor" : [ 0.6, 0.6, 0.6, 0.0 ],
					"ignoreclick" : 1,
					"patching_rect" : [ 515.0, 74.0, 112.0, 17.0 ],
					"numinlets" : 1,
					"bgovercolor" : [ 0.698039, 0.698039, 0.698039, 0.0 ],
					"truncate" : 1,
					"fontface" : 1,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"tosymbol" : 1,
					"bgcolor" : [ 0.74902, 0.74902, 0.74902, 0.0 ],
					"hidden" : 0,
					"rounded" : 0.0,
					"underline" : 0,
					"borderoncolor" : [ 0.4, 0.4, 0.4, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"bgoveroncolor" : [ 0.5, 0.5, 0.5, 1.0 ],
					"align" : 0,
					"mode" : 0,
					"textoveroncolor" : [ 0.9, 0.9, 0.9, 1.0 ],
					"border" : 0,
					"spacing_x" : 4.0,
					"outlettype" : [ "", "", "int" ],
					"bgoncolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"text" : "Input Source",
					"blinktime" : 150
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "substitute append text",
					"id" : "obj-20",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 650.0, 52.0, 113.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus optionname 2",
					"id" : "obj-45",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 650.0, 30.0, 116.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "textbutton",
					"hint" : "",
					"spacing_y" : 4.0,
					"textoncolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"id" : "obj-96",
					"presentation_rect" : [ 14.0, 84.0, 91.0, 20.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"texton" : "Button On",
					"outputmode" : 1,
					"active" : 1,
					"fontlink" : 0,
					"textovercolor" : [ 0.101961, 0.101961, 0.101961, 0.0 ],
					"bordercolor" : [ 0.6, 0.6, 0.6, 0.0 ],
					"ignoreclick" : 1,
					"patching_rect" : [ 650.0, 74.0, 112.0, 17.0 ],
					"numinlets" : 1,
					"bgovercolor" : [ 0.698039, 0.698039, 0.698039, 0.0 ],
					"truncate" : 1,
					"fontface" : 1,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"tosymbol" : 1,
					"bgcolor" : [ 0.74902, 0.74902, 0.74902, 0.0 ],
					"hidden" : 0,
					"rounded" : 0.0,
					"underline" : 0,
					"borderoncolor" : [ 0.4, 0.4, 0.4, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"bgoveroncolor" : [ 0.5, 0.5, 0.5, 1.0 ],
					"align" : 0,
					"mode" : 0,
					"textoveroncolor" : [ 0.9, 0.9, 0.9, 1.0 ],
					"border" : 0,
					"spacing_x" : 4.0,
					"outlettype" : [ "", "", "int" ],
					"bgoncolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"text" : "Output Destination",
					"blinktime" : 150
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "substitute append text",
					"id" : "obj-97",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 380.0, 52.0, 113.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus optionname 0",
					"id" : "obj-13",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 380.0, 30.0, 116.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : ";\rdsp driver setup",
					"linecount" : 2,
					"id" : "obj-89",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"ignoreclick" : 0,
					"patching_rect" : [ 425.0, 399.0, 85.0, 28.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 0.867, 0.867, 0.867, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 1,
					"bgcolor2" : [ 0.867, 0.867, 0.867, 1.0 ],
					"gradient" : 0,
					"outlettype" : [ "" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Signal Vector Size",
					"id" : "obj-46",
					"presentation_rect" : [ 14.0, 156.0, 91.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 77.0, 255.0, 91.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "I/O Vector Size",
					"id" : "obj-42",
					"presentation_rect" : [ 14.0, 138.0, 75.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 79.0, 195.0, 75.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "route clear append",
					"id" : "obj-59",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 676.0, 238.0, 98.0, 18.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 3,
					"outlettype" : [ "", "", "" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "route clear append",
					"id" : "obj-55",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 305.0, 298.0, 98.0, 18.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 3,
					"outlettype" : [ "", "", "" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "route clear append",
					"id" : "obj-50",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 166.0, 298.0, 98.0, 18.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 3,
					"outlettype" : [ "", "", "" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus output 2",
					"id" : "obj-79",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 305.0, 471.0, 91.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"hint" : "",
					"prototypename" : "Arial9",
					"discolor" : [ 0.43, 0.43, 0.43, 1.0 ],
					"depth" : 0,
					"pattrmode" : 1,
					"labelclick" : 0,
					"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
					"id" : "obj-80",
					"presentation_rect" : [ 189.0, 328.0, 58.939552, 17.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
					"arrowlink" : 0,
					"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
					"prefix_mode" : 2,
					"ignoreclick" : 0,
					"textcolor2" : [ 0.15, 0.15, 0.15, 1.0 ],
					"types" : [  ],
					"arrow" : 1,
					"patching_rect" : [ 305.0, 450.0, 50.939552, 17.0 ],
					"numinlets" : 1,
					"truncate" : 1,
					"autopopulate" : 0,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"hidden" : 0,
					"rounded" : 8,
					"underline" : 0,
					"togcolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"align" : 0,
					"menumode" : 0,
					"bgcolor2" : [ 1.0, 1.0, 1.0, 1.0 ],
					"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
					"arrowframe" : 0,
					"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"outlettype" : [ "int", "", "" ],
					"showdotfiles" : 0,
					"prefix" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Channel 2",
					"id" : "obj-73",
					"presentation_rect" : [ 14.0, 329.0, 55.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 359.0, 390.0, 55.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus input 2",
					"id" : "obj-76",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 305.0, 411.0, 84.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"hint" : "",
					"prototypename" : "Arial9",
					"discolor" : [ 0.43, 0.43, 0.43, 1.0 ],
					"depth" : 0,
					"pattrmode" : 1,
					"labelclick" : 0,
					"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
					"id" : "obj-77",
					"presentation_rect" : [ 70.0, 328.0, 58.939552, 17.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
					"arrowlink" : 0,
					"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
					"prefix_mode" : 2,
					"ignoreclick" : 0,
					"textcolor2" : [ 0.15, 0.15, 0.15, 1.0 ],
					"types" : [  ],
					"arrow" : 1,
					"patching_rect" : [ 305.0, 390.0, 50.939552, 17.0 ],
					"numinlets" : 1,
					"truncate" : 1,
					"autopopulate" : 0,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"hidden" : 0,
					"rounded" : 8,
					"underline" : 0,
					"togcolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"align" : 0,
					"menumode" : 0,
					"bgcolor2" : [ 1.0, 1.0, 1.0, 1.0 ],
					"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
					"arrowframe" : 0,
					"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"outlettype" : [ "int", "", "" ],
					"showdotfiles" : 0,
					"prefix" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Channel 1",
					"id" : "obj-67",
					"presentation_rect" : [ 14.0, 310.0, 55.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 239.0, 390.0, 55.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus output 1",
					"id" : "obj-68",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 185.0, 471.0, 91.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"hint" : "",
					"prototypename" : "Arial9",
					"discolor" : [ 0.43, 0.43, 0.43, 1.0 ],
					"depth" : 0,
					"pattrmode" : 1,
					"labelclick" : 0,
					"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
					"id" : "obj-69",
					"presentation_rect" : [ 189.0, 310.0, 58.939552, 17.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
					"arrowlink" : 0,
					"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
					"prefix_mode" : 2,
					"ignoreclick" : 0,
					"textcolor2" : [ 0.15, 0.15, 0.15, 1.0 ],
					"types" : [  ],
					"arrow" : 1,
					"patching_rect" : [ 185.0, 450.0, 50.939552, 17.0 ],
					"numinlets" : 1,
					"truncate" : 1,
					"autopopulate" : 0,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"hidden" : 0,
					"rounded" : 8,
					"underline" : 0,
					"togcolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"align" : 0,
					"menumode" : 0,
					"bgcolor2" : [ 1.0, 1.0, 1.0, 1.0 ],
					"items" : [ "Off", ",", 1, "output", ",", 2, "output" ],
					"arrowframe" : 0,
					"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"outlettype" : [ "int", "", "" ],
					"showdotfiles" : 0,
					"prefix" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus input 1",
					"id" : "obj-70",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 185.0, 411.0, 84.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"hint" : "",
					"prototypename" : "Arial9",
					"discolor" : [ 0.43, 0.43, 0.43, 1.0 ],
					"depth" : 0,
					"pattrmode" : 1,
					"labelclick" : 0,
					"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
					"id" : "obj-71",
					"presentation_rect" : [ 70.0, 310.0, 58.939552, 17.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
					"arrowlink" : 0,
					"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
					"prefix_mode" : 2,
					"ignoreclick" : 0,
					"textcolor2" : [ 0.15, 0.15, 0.15, 1.0 ],
					"types" : [  ],
					"arrow" : 1,
					"patching_rect" : [ 185.0, 390.0, 50.939552, 17.0 ],
					"numinlets" : 1,
					"truncate" : 1,
					"autopopulate" : 0,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"hidden" : 0,
					"rounded" : 8,
					"underline" : 0,
					"togcolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"align" : 0,
					"menumode" : 0,
					"bgcolor2" : [ 1.0, 1.0, 1.0, 1.0 ],
					"items" : [ "Off", ",", 1, "input", ",", 2, "input" ],
					"arrowframe" : 0,
					"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"outlettype" : [ "int", "", "" ],
					"showdotfiles" : 0,
					"prefix" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Vector Optimization",
					"id" : "obj-63",
					"presentation_rect" : [ 14.0, 260.0, 97.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 695.0, 195.0, 97.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus optimize",
					"id" : "obj-64",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 676.0, 216.0, 92.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "toggle",
					"id" : "obj-65",
					"presentation_rect" : [ 125.0, 260.0, 15.0, 15.0 ],
					"background" : 0,
					"checkedcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"bordercolor" : [ 0.741176, 0.741176, 0.803922, 0.74902 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 676.0, 195.0, 16.0, 16.0 ],
					"numinlets" : 1,
					"presentation" : 1,
					"bgcolor" : [ 0.352941, 0.352941, 0.501961, 0.74902 ],
					"hidden" : 0,
					"numoutlets" : 1,
					"outlettype" : [ "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "in Audio Interrupt",
					"linecount" : 2,
					"id" : "obj-60",
					"presentation_rect" : [ 140.0, 175.0, 88.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 324.0, 255.0, 51.0, 28.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus takeover",
					"id" : "obj-61",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 305.0, 276.0, 93.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "toggle",
					"id" : "obj-62",
					"presentation_rect" : [ 227.0, 175.0, 15.0, 15.0 ],
					"background" : 0,
					"checkedcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"bordercolor" : [ 0.741176, 0.741176, 0.803922, 0.74902 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 305.0, 255.0, 16.0, 16.0 ],
					"numinlets" : 1,
					"presentation" : 1,
					"bgcolor" : [ 0.352941, 0.352941, 0.501961, 0.74902 ],
					"hidden" : 0,
					"numoutlets" : 1,
					"outlettype" : [ "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Scheduler in Overdrive",
					"id" : "obj-56",
					"presentation_rect" : [ 14.0, 175.0, 110.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 185.0, 255.0, 112.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus overdrive",
					"id" : "obj-57",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 166.0, 276.0, 96.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "toggle",
					"id" : "obj-58",
					"presentation_rect" : [ 125.0, 175.0, 15.0, 15.0 ],
					"background" : 0,
					"checkedcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"bordercolor" : [ 0.741176, 0.741176, 0.803922, 0.74902 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 166.0, 255.0, 16.0, 16.0 ],
					"numinlets" : 1,
					"presentation" : 1,
					"bgcolor" : [ 0.352941, 0.352941, 0.501961, 0.74902 ],
					"hidden" : 0,
					"numoutlets" : 1,
					"outlettype" : [ "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "CPU Limit (%)",
					"id" : "obj-51",
					"presentation_rect" : [ 14.0, 224.0, 72.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 595.0, 195.0, 70.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus cpulimit",
					"id" : "obj-52",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 545.0, 216.0, 88.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "number",
					"hint" : "",
					"id" : "obj-53",
					"presentation_rect" : [ 109.0, 224.0, 30.848198, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"hbgcolor" : [ 0.372549, 0.352941, 0.439216, 1.0 ],
					"mouseup" : 0,
					"bordercolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"minimum" : 0,
					"format" : 0,
					"htextcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 545.0, 195.0, 28.0, 17.0 ],
					"numinlets" : 1,
					"maximum" : 100,
					"fontface" : 0,
					"presentation" : 1,
					"triscale" : 0.6,
					"fontsize" : 9.27615,
					"bgcolor" : [ 0.368627, 0.360784, 0.501961, 0.74902 ],
					"hidden" : 0,
					"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"numoutlets" : 2,
					"cantchange" : 0,
					"tricolor" : [ 0.75, 0.75, 0.75, 1.0 ],
					"triangle" : 1,
					"outlettype" : [ "int", "bang" ],
					"outputonclick" : 0,
					"htricolor" : [ 0.87, 0.82, 0.24, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus sigvs",
					"id" : "obj-47",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 35.0, 276.0, 77.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"hint" : "",
					"prototypename" : "Arial9",
					"discolor" : [ 0.43, 0.43, 0.43, 1.0 ],
					"depth" : 0,
					"pattrmode" : 1,
					"labelclick" : 0,
					"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
					"id" : "obj-48",
					"presentation_rect" : [ 108.0, 155.0, 44.939552, 17.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
					"arrowlink" : 0,
					"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
					"prefix_mode" : 2,
					"ignoreclick" : 0,
					"textcolor2" : [ 0.15, 0.15, 0.15, 1.0 ],
					"types" : [  ],
					"arrow" : 1,
					"patching_rect" : [ 35.0, 255.0, 43.939552, 17.0 ],
					"numinlets" : 1,
					"truncate" : 1,
					"autopopulate" : 0,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"hidden" : 0,
					"rounded" : 8,
					"underline" : 0,
					"togcolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"align" : 0,
					"menumode" : 0,
					"bgcolor2" : [ 1.0, 1.0, 1.0, 1.0 ],
					"items" : [ 1, ",", 2, ",", 4, ",", 8, ",", 16, ",", 32, ",", 64, ",", 128, ",", 256, ",", 512, ",", 1024, ",", 2048, ",", 4096, ",", 8192 ],
					"arrowframe" : 0,
					"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"outlettype" : [ "int", "", "" ],
					"showdotfiles" : 0,
					"prefix" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus iovs",
					"id" : "obj-43",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 35.0, 216.0, 71.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"hint" : "",
					"prototypename" : "Arial9",
					"discolor" : [ 0.43, 0.43, 0.43, 1.0 ],
					"depth" : 0,
					"pattrmode" : 1,
					"labelclick" : 0,
					"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
					"id" : "obj-44",
					"presentation_rect" : [ 108.0, 137.0, 44.939552, 17.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
					"arrowlink" : 0,
					"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
					"prefix_mode" : 2,
					"ignoreclick" : 0,
					"textcolor2" : [ 0.15, 0.15, 0.15, 1.0 ],
					"types" : [  ],
					"arrow" : 1,
					"patching_rect" : [ 35.0, 195.0, 43.939552, 17.0 ],
					"numinlets" : 1,
					"truncate" : 1,
					"autopopulate" : 0,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"hidden" : 0,
					"rounded" : 8,
					"underline" : 0,
					"togcolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"align" : 0,
					"menumode" : 0,
					"bgcolor2" : [ 1.0, 1.0, 1.0, 1.0 ],
					"items" : [ 16, ",", 32, ",", 64, ",", 128, ",", 256, ",", 512, ",", 1024, ",", 2048, ",", 4096 ],
					"arrowframe" : 0,
					"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"outlettype" : [ "int", "", "" ],
					"showdotfiles" : 0,
					"prefix" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Output Channels",
					"id" : "obj-38",
					"presentation_rect" : [ 134.0, 291.0, 85.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 85.0, 472.0, 85.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Input Channels",
					"id" : "obj-37",
					"presentation_rect" : [ 14.0, 291.0, 78.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 85.0, 412.0, 78.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "number",
					"hint" : "",
					"id" : "obj-34",
					"presentation_rect" : [ 217.0, 291.0, 31.0, 17.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"hbgcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"mouseup" : 0,
					"bordercolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"minimum" : "<none>",
					"format" : 0,
					"htextcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"ignoreclick" : 1,
					"patching_rect" : [ 35.0, 472.0, 28.0, 17.0 ],
					"numinlets" : 1,
					"maximum" : "<none>",
					"fontface" : 0,
					"presentation" : 1,
					"triscale" : 1.0,
					"fontsize" : 9.27615,
					"bgcolor" : [ 0.368627, 0.360784, 0.501961, 0.74902 ],
					"hidden" : 0,
					"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"numoutlets" : 2,
					"cantchange" : 0,
					"tricolor" : [ 0.75, 0.75, 0.75, 1.0 ],
					"triangle" : 0,
					"outlettype" : [ "int", "bang" ],
					"outputonclick" : 0,
					"htricolor" : [ 0.87, 0.82, 0.24, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus numoutputs",
					"id" : "obj-35",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 35.0, 450.0, 107.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "number",
					"hint" : "",
					"id" : "obj-31",
					"presentation_rect" : [ 98.0, 291.0, 31.0, 17.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"hbgcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"mouseup" : 0,
					"bordercolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"minimum" : "<none>",
					"format" : 0,
					"htextcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"ignoreclick" : 1,
					"patching_rect" : [ 35.0, 412.0, 28.0, 17.0 ],
					"numinlets" : 1,
					"maximum" : "<none>",
					"fontface" : 0,
					"presentation" : 1,
					"triscale" : 1.0,
					"fontsize" : 9.27615,
					"bgcolor" : [ 0.368627, 0.360784, 0.501961, 0.74902 ],
					"hidden" : 0,
					"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"numoutlets" : 2,
					"cantchange" : 0,
					"tricolor" : [ 0.75, 0.75, 0.75, 1.0 ],
					"triangle" : 0,
					"outlettype" : [ "int", "bang" ],
					"outputonclick" : 0,
					"htricolor" : [ 0.87, 0.82, 0.24, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus numinputs",
					"id" : "obj-33",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 35.0, 390.0, 101.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Sampling Rate",
					"id" : "obj-39",
					"presentation_rect" : [ 168.0, 138.0, 75.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 218.0, 195.0, 75.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus sr",
					"id" : "obj-40",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 170.0, 216.0, 62.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"hint" : "",
					"prototypename" : "Arial9",
					"discolor" : [ 0.43, 0.43, 0.43, 1.0 ],
					"depth" : 0,
					"pattrmode" : 1,
					"labelclick" : 0,
					"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
					"id" : "obj-41",
					"presentation_rect" : [ 168.0, 155.0, 55.939552, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
					"arrowlink" : 0,
					"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
					"prefix_mode" : 2,
					"ignoreclick" : 0,
					"textcolor2" : [ 0.15, 0.15, 0.15, 1.0 ],
					"types" : [  ],
					"arrow" : 1,
					"patching_rect" : [ 170.0, 195.0, 43.939552, 17.0 ],
					"numinlets" : 1,
					"truncate" : 1,
					"autopopulate" : 0,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"hidden" : 0,
					"rounded" : 8,
					"underline" : 0,
					"togcolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"align" : 0,
					"menumode" : 0,
					"bgcolor2" : [ 1.0, 1.0, 1.0, 1.0 ],
					"items" : [ 96000, ",", 88200, ",", 48000, ",", 44100 ],
					"arrowframe" : 0,
					"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"outlettype" : [ "int", "", "" ],
					"showdotfiles" : 0,
					"prefix" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Function Calls",
					"id" : "obj-32",
					"presentation_rect" : [ 143.0, 242.0, 74.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 668.0, 310.0, 74.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "number",
					"hint" : "",
					"id" : "obj-30",
					"presentation_rect" : [ 217.0, 242.0, 31.0, 17.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"hbgcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"mouseup" : 0,
					"bordercolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"minimum" : "<none>",
					"format" : 0,
					"htextcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"ignoreclick" : 1,
					"patching_rect" : [ 671.0, 292.0, 28.0, 17.0 ],
					"numinlets" : 1,
					"maximum" : "<none>",
					"fontface" : 0,
					"presentation" : 1,
					"triscale" : 1.0,
					"fontsize" : 9.27615,
					"bgcolor" : [ 0.368627, 0.360784, 0.501961, 0.74902 ],
					"hidden" : 0,
					"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"numoutlets" : 2,
					"cantchange" : 0,
					"tricolor" : [ 0.75, 0.75, 0.75, 1.0 ],
					"triangle" : 0,
					"outlettype" : [ "int", "bang" ],
					"outputonclick" : 0,
					"htricolor" : [ 0.87, 0.82, 0.24, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Signals Used",
					"linecount" : 2,
					"id" : "obj-26",
					"presentation_rect" : [ 14.0, 242.0, 69.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 602.0, 310.0, 62.0, 28.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "number",
					"hint" : "",
					"id" : "obj-27",
					"presentation_rect" : [ 109.0, 242.0, 31.0, 17.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"hbgcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"mouseup" : 0,
					"bordercolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"minimum" : "<none>",
					"format" : 0,
					"htextcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"ignoreclick" : 1,
					"patching_rect" : [ 620.0, 292.0, 28.0, 17.0 ],
					"numinlets" : 1,
					"maximum" : "<none>",
					"fontface" : 0,
					"presentation" : 1,
					"triscale" : 1.0,
					"fontsize" : 9.27615,
					"bgcolor" : [ 0.368627, 0.360784, 0.501961, 0.74902 ],
					"hidden" : 0,
					"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"numoutlets" : 2,
					"cantchange" : 0,
					"tricolor" : [ 0.75, 0.75, 0.75, 1.0 ],
					"triangle" : 0,
					"outlettype" : [ "int", "bang" ],
					"outputonclick" : 0,
					"htricolor" : [ 0.87, 0.82, 0.24, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus info",
					"id" : "obj-28",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 620.0, 270.0, 70.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "loadbang",
					"id" : "obj-25",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 455.0, 195.0, 53.0, 18.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 1,
					"outlettype" : [ "bang" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "CPU Utilization (%)",
					"id" : "obj-23",
					"presentation_rect" : [ 14.0, 206.0, 97.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 506.0, 262.0, 96.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "flonum",
					"hint" : "",
					"id" : "obj-22",
					"presentation_rect" : [ 109.0, 206.0, 31.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"numdecimalplaces" : 0,
					"hbgcolor" : [ 0.372549, 0.360784, 0.501961, 0.74902 ],
					"mouseup" : 0,
					"bordercolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"minimum" : "<none>",
					"htextcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"ignoreclick" : 1,
					"patching_rect" : [ 455.0, 261.0, 30.0, 17.0 ],
					"numinlets" : 1,
					"maximum" : "<none>",
					"fontface" : 0,
					"presentation" : 1,
					"triscale" : 0.7,
					"fontsize" : 9.27615,
					"bgcolor" : [ 0.368627, 0.360784, 0.501961, 0.74902 ],
					"hidden" : 0,
					"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"numoutlets" : 2,
					"cantchange" : 0,
					"tricolor" : [ 0.75, 0.75, 0.75, 1.0 ],
					"triangle" : 0,
					"outlettype" : [ "float", "bang" ],
					"outputonclick" : 0,
					"htricolor" : [ 0.87, 0.82, 0.24, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus cpu",
					"id" : "obj-15",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 455.0, 239.0, 70.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "metro 250",
					"id" : "obj-8",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 455.0, 217.0, 58.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 1,
					"outlettype" : [ "bang" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "textbutton",
					"hint" : "",
					"spacing_y" : 4.0,
					"textoncolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"id" : "obj-18",
					"presentation_rect" : [ 14.0, 48.0, 91.0, 20.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"texton" : "Button On",
					"outputmode" : 1,
					"active" : 1,
					"fontlink" : 0,
					"textovercolor" : [ 0.101961, 0.101961, 0.101961, 0.0 ],
					"bordercolor" : [ 0.6, 0.6, 0.6, 0.0 ],
					"ignoreclick" : 1,
					"patching_rect" : [ 380.0, 74.0, 112.0, 17.0 ],
					"numinlets" : 1,
					"bgovercolor" : [ 0.698039, 0.698039, 0.698039, 0.0 ],
					"truncate" : 1,
					"fontface" : 1,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"tosymbol" : 1,
					"bgcolor" : [ 0.74902, 0.74902, 0.74902, 0.0 ],
					"hidden" : 0,
					"rounded" : 0.0,
					"underline" : 0,
					"borderoncolor" : [ 0.4, 0.4, 0.4, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"bgoveroncolor" : [ 0.5, 0.5, 0.5, 1.0 ],
					"align" : 0,
					"mode" : 0,
					"textoveroncolor" : [ 0.9, 0.9, 0.9, 1.0 ],
					"border" : 0,
					"spacing_x" : 4.0,
					"outlettype" : [ "", "", "int" ],
					"bgoncolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"text" : "Input Device",
					"blinktime" : 150
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Driver",
					"id" : "obj-17",
					"presentation_rect" : [ 14.0, 32.0, 37.0, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 299.0, 105.0, 37.0, 17.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 0.258824, 0.258824, 0.258824, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"hint" : "",
					"text" : "Audio",
					"id" : "obj-16",
					"presentation_rect" : [ 14.0, 12.0, 44.0, 20.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"ignoreclick" : 1,
					"patching_rect" : [ 328.0, 50.0, 44.0, 20.0 ],
					"numinlets" : 1,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 11.595187,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"hidden" : 0,
					"underline" : 0,
					"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"numoutlets" : 0,
					"frgb" : [ 1.0, 1.0, 1.0, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus option 2",
					"id" : "obj-4",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 650.0, 126.0, 90.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"hint" : "",
					"prototypename" : "Arial9",
					"discolor" : [ 0.43, 0.43, 0.43, 1.0 ],
					"depth" : 0,
					"pattrmode" : 1,
					"labelclick" : 0,
					"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
					"id" : "obj-5",
					"presentation_rect" : [ 108.0, 85.0, 139.939545, 17.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
					"arrowlink" : 0,
					"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
					"prefix_mode" : 2,
					"ignoreclick" : 0,
					"textcolor2" : [ 0.15, 0.15, 0.15, 1.0 ],
					"types" : [  ],
					"arrow" : 1,
					"patching_rect" : [ 650.0, 105.0, 50.939552, 17.0 ],
					"numinlets" : 1,
					"truncate" : 1,
					"autopopulate" : 0,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"hidden" : 0,
					"rounded" : 8,
					"underline" : 0,
					"togcolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"align" : 0,
					"menumode" : 0,
					"bgcolor2" : [ 1.0, 1.0, 1.0, 1.0 ],
					"items" : "Headphones",
					"arrowframe" : 0,
					"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"outlettype" : [ "int", "", "" ],
					"showdotfiles" : 0,
					"prefix" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus option 1",
					"id" : "obj-2",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 515.0, 126.0, 90.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"hint" : "",
					"prototypename" : "Arial9",
					"discolor" : [ 0.43, 0.43, 0.43, 1.0 ],
					"depth" : 0,
					"pattrmode" : 1,
					"labelclick" : 0,
					"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
					"id" : "obj-3",
					"presentation_rect" : [ 108.0, 67.0, 139.939545, 17.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
					"arrowlink" : 0,
					"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
					"prefix_mode" : 2,
					"ignoreclick" : 0,
					"textcolor2" : [ 0.15, 0.15, 0.15, 1.0 ],
					"types" : [  ],
					"arrow" : 1,
					"patching_rect" : [ 515.0, 105.0, 50.939552, 17.0 ],
					"numinlets" : 1,
					"truncate" : 1,
					"autopopulate" : 0,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"hidden" : 0,
					"rounded" : 8,
					"underline" : 0,
					"togcolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"align" : 0,
					"menumode" : 0,
					"bgcolor2" : [ 1.0, 1.0, 1.0, 1.0 ],
					"items" : "Internal microphone",
					"arrowframe" : 0,
					"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"outlettype" : [ "int", "", "" ],
					"showdotfiles" : 0,
					"prefix" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus option 0",
					"id" : "obj-11",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 380.0, 126.0, 90.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"hint" : "",
					"prototypename" : "Arial9",
					"discolor" : [ 0.43, 0.43, 0.43, 1.0 ],
					"depth" : 0,
					"pattrmode" : 1,
					"labelclick" : 0,
					"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
					"id" : "obj-12",
					"presentation_rect" : [ 108.0, 49.0, 139.939545, 17.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
					"arrowlink" : 0,
					"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
					"prefix_mode" : 2,
					"ignoreclick" : 0,
					"textcolor2" : [ 0.15, 0.15, 0.15, 1.0 ],
					"types" : [  ],
					"arrow" : 1,
					"patching_rect" : [ 380.0, 105.0, 50.939552, 17.0 ],
					"numinlets" : 1,
					"truncate" : 1,
					"autopopulate" : 0,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"hidden" : 0,
					"rounded" : 8,
					"underline" : 0,
					"togcolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"align" : 0,
					"menumode" : 0,
					"bgcolor2" : [ 1.0, 1.0, 1.0, 1.0 ],
					"items" : [ "Built-in Microphone", ",", "Built-in Input", ",", "Soundflower (2ch)", ",", "Soundflower (16ch)" ],
					"arrowframe" : 0,
					"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"outlettype" : [ "int", "", "" ],
					"showdotfiles" : 0,
					"prefix" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus driver",
					"id" : "obj-9",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 245.0, 126.0, 79.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"hint" : "",
					"prototypename" : "Arial9",
					"discolor" : [ 0.43, 0.43, 0.43, 1.0 ],
					"depth" : 0,
					"pattrmode" : 1,
					"labelclick" : 0,
					"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
					"id" : "obj-10",
					"presentation_rect" : [ 108.0, 31.0, 139.939545, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
					"arrowlink" : 0,
					"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
					"prefix_mode" : 2,
					"ignoreclick" : 0,
					"textcolor2" : [ 0.15, 0.15, 0.15, 1.0 ],
					"types" : [  ],
					"arrow" : 1,
					"patching_rect" : [ 245.0, 105.0, 50.939552, 17.0 ],
					"numinlets" : 1,
					"truncate" : 1,
					"autopopulate" : 0,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"hidden" : 0,
					"rounded" : 8,
					"underline" : 0,
					"togcolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"align" : 0,
					"menumode" : 0,
					"bgcolor2" : [ 1.0, 1.0, 1.0, 1.0 ],
					"items" : [ "None", ",", "CoreAudio", "Built-in Output", ",", "CoreAudio", "Soundflower (2ch)", ",", "CoreAudio", "Soundflower (16ch)", ",", "NonRealTime", ",", "ad_rewire", ",", "Live" ],
					"arrowframe" : 0,
					"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"outlettype" : [ "int", "", "" ],
					"showdotfiles" : 0,
					"prefix" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "adstatus switch",
					"id" : "obj-1",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"fontname" : "Arial",
					"background" : 0,
					"color" : [ 0.7, 0.7, 0.7, 1.0 ],
					"ignoreclick" : 0,
					"patching_rect" : [ 245.0, 51.0, 82.0, 18.0 ],
					"numinlets" : 2,
					"fontface" : 0,
					"presentation" : 0,
					"fontsize" : 10.435669,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"hidden" : 0,
					"textcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 2,
					"outlettype" : [ "", "int" ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"hint" : "",
					"prototypename" : "Arial9",
					"discolor" : [ 0.43, 0.43, 0.43, 1.0 ],
					"depth" : 0,
					"pattrmode" : 1,
					"labelclick" : 0,
					"hltcolor" : [ 0.827451, 0.827451, 0.827451, 1.0 ],
					"id" : "obj-36",
					"presentation_rect" : [ 108.0, 13.0, 44.939552, 17.0 ],
					"fontname" : "Arial Bold",
					"background" : 0,
					"arrowcolor" : [ 0.694118, 0.694118, 0.694118, 1.0 ],
					"arrowlink" : 0,
					"arrowbgcolor" : [ 0.941176, 0.941176, 0.941176, 0.74902 ],
					"prefix_mode" : 2,
					"ignoreclick" : 0,
					"textcolor2" : [ 0.15, 0.15, 0.15, 1.0 ],
					"types" : [  ],
					"arrow" : 1,
					"patching_rect" : [ 245.0, 30.0, 50.939552, 17.0 ],
					"numinlets" : 1,
					"truncate" : 1,
					"autopopulate" : 0,
					"fontface" : 0,
					"presentation" : 1,
					"fontsize" : 9.27615,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.74902 ],
					"hidden" : 0,
					"rounded" : 8,
					"underline" : 0,
					"togcolor" : [ 0.55, 0.55, 0.55, 1.0 ],
					"textcolor" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"numoutlets" : 3,
					"align" : 0,
					"menumode" : 0,
					"bgcolor2" : [ 1.0, 1.0, 1.0, 1.0 ],
					"items" : [ "Off", ",", "On" ],
					"arrowframe" : 0,
					"framecolor" : [ 0.501961, 0.501961, 0.501961, 0.129412 ],
					"outlettype" : [ "int", "", "" ],
					"showdotfiles" : 0,
					"prefix" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"prototypename" : "Arial9",
					"id" : "obj-105",
					"presentation_rect" : [ 146.0, 210.130554, 101.312851, 10.0 ],
					"background" : 1,
					"bordercolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 456.0, 296.0, 124.0, 4.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 1,
					"bgcolor" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"hidden" : 0,
					"rounded" : 7,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 0,
					"shadow" : -1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"prototypename" : "Arial9",
					"id" : "obj-19",
					"presentation_rect" : [ 217.0, 242.130554, 31.0, 17.0 ],
					"background" : 1,
					"bordercolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 701.0, 295.130554, 19.0, 12.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 1,
					"bgcolor" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"hidden" : 0,
					"rounded" : 9,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 0,
					"shadow" : -1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"prototypename" : "Arial9",
					"id" : "obj-95",
					"presentation_rect" : [ 110.0, 206.130554, 30.0, 17.0 ],
					"background" : 1,
					"bordercolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 486.0, 264.130554, 19.0, 12.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 1,
					"bgcolor" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"hidden" : 0,
					"rounded" : 9,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 0,
					"shadow" : -1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"prototypename" : "Arial9",
					"id" : "obj-92",
					"presentation_rect" : [ 217.0, 291.130554, 31.0, 17.0 ],
					"background" : 1,
					"bordercolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 64.0, 475.0, 19.0, 12.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 1,
					"bgcolor" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"hidden" : 0,
					"rounded" : 9,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 0,
					"shadow" : -1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"prototypename" : "Arial9",
					"id" : "obj-74",
					"presentation_rect" : [ 98.0, 291.130554, 31.0, 17.0 ],
					"background" : 1,
					"bordercolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 64.0, 415.0, 19.0, 12.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 1,
					"bgcolor" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"hidden" : 0,
					"rounded" : 9,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 0,
					"shadow" : -1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"prototypename" : "Arial9",
					"id" : "obj-54",
					"presentation_rect" : [ 109.0, 242.130554, 31.0, 17.0 ],
					"background" : 1,
					"bordercolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 650.0, 295.130554, 19.0, 12.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 1,
					"bgcolor" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"hidden" : 0,
					"rounded" : 9,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 0,
					"shadow" : -1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"prototypename" : "Arial9",
					"id" : "obj-282",
					"presentation_rect" : [ 109.0, 224.130554, 31.0, 17.0 ],
					"background" : 1,
					"bordercolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 574.0, 197.0, 19.0, 12.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 1,
					"bgcolor" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"hidden" : 0,
					"rounded" : 9,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 0,
					"shadow" : -1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"hint" : "",
					"prototypename" : "Arial9",
					"id" : "obj-86",
					"presentation_rect" : [ 13.0, 285.0, 243.0, 94.0 ],
					"background" : 1,
					"bordercolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 39.0, 501.0, 20.0, 20.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 1,
					"bgcolor" : [ 0.658824, 0.658824, 0.74902, 1.0 ],
					"hidden" : 0,
					"rounded" : 16,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 0,
					"shadow" : -1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"hint" : "",
					"prototypename" : "Arial9",
					"id" : "obj-21",
					"presentation_rect" : [ 13.0, 200.0, 243.0, 81.0 ],
					"background" : 1,
					"bordercolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 365.0, 264.0, 20.0, 20.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 1,
					"bgcolor" : [ 0.658824, 0.658824, 0.74902, 1.0 ],
					"hidden" : 0,
					"rounded" : 16,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 0,
					"shadow" : -1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"hint" : "",
					"prototypename" : "Arial9",
					"id" : "obj-14",
					"presentation_rect" : [ 12.0, 131.0, 243.0, 65.0 ],
					"background" : 1,
					"bordercolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 316.0, 194.0, 20.0, 20.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 1,
					"bgcolor" : [ 0.658824, 0.658824, 0.74902, 1.0 ],
					"hidden" : 0,
					"rounded" : 16,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 0,
					"shadow" : -1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"hint" : "",
					"prototypename" : "Arial9",
					"id" : "obj-212",
					"presentation_rect" : [ 12.0, 7.0, 243.0, 120.0 ],
					"background" : 1,
					"bordercolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 866.0, 122.0, 20.0, 20.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 1,
					"bgcolor" : [ 0.658824, 0.658824, 0.74902, 1.0 ],
					"hidden" : 0,
					"rounded" : 16,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 0,
					"shadow" : -1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"hint" : "",
					"prototypename" : "Arial9",
					"id" : "obj-24",
					"presentation_rect" : [ 0.452972, 0.0, 267.0, 390.0 ],
					"background" : 1,
					"bordercolor" : [ 0.360784, 0.360784, 0.360784, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 139.0, 74.0, 20.0, 20.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 1,
					"bgcolor" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"hidden" : 0,
					"rounded" : 0,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 0,
					"shadow" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"hint" : "",
					"prototypename" : "Arial9",
					"id" : "obj-107",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"background" : 1,
					"bordercolor" : [ 0.670588, 0.670588, 0.74902, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 20.0, 180.0, 405.0, 165.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 0,
					"bgcolor" : [ 0.741176, 0.741176, 0.803922, 0.0 ],
					"hidden" : 0,
					"rounded" : 20,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 1,
					"shadow" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"hint" : "",
					"prototypename" : "Arial9",
					"id" : "obj-88",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"background" : 1,
					"bordercolor" : [ 0.670588, 0.670588, 0.74902, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 230.0, 15.0, 663.0, 151.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 0,
					"bgcolor" : [ 0.741176, 0.741176, 0.803922, 0.0 ],
					"hidden" : 0,
					"rounded" : 20,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 1,
					"shadow" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"hint" : "",
					"prototypename" : "Arial9",
					"id" : "obj-109",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"background" : 1,
					"bordercolor" : [ 0.670588, 0.670588, 0.74902, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 440.0, 180.0, 360.0, 165.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 0,
					"bgcolor" : [ 0.741176, 0.741176, 0.803922, 0.0 ],
					"hidden" : 0,
					"rounded" : 20,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 1,
					"shadow" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"hint" : "",
					"prototypename" : "Arial9",
					"id" : "obj-110",
					"presentation_rect" : [ 0.0, 0.0, 0.0, 0.0 ],
					"background" : 1,
					"bordercolor" : [ 0.670588, 0.670588, 0.74902, 1.0 ],
					"angle" : 0.0,
					"ignoreclick" : 1,
					"patching_rect" : [ 20.0, 360.0, 525.0, 180.0 ],
					"numinlets" : 1,
					"grad1" : [ 0.847059, 0.847059, 0.847059, 1.0 ],
					"presentation" : 0,
					"bgcolor" : [ 0.741176, 0.741176, 0.803922, 0.0 ],
					"hidden" : 0,
					"rounded" : 20,
					"numoutlets" : 0,
					"grad2" : [ 0.741176, 0.741176, 0.803922, 1.0 ],
					"mode" : 0,
					"border" : 1,
					"shadow" : 1
				}

			}
 ],
		"lines" : [ 			{
				"patchline" : 				{
					"source" : [ "obj-7", 0 ],
					"destination" : [ "obj-6", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-87", 0 ],
					"destination" : [ "obj-90", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 780.5, 147.0, 768.0, 147.0, 768.0, 101.0, 780.5, 101.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-90", 0 ],
					"destination" : [ "obj-87", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-6", 0 ],
					"destination" : [ "obj-72", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-112", 0 ],
					"destination" : [ "obj-114", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-113", 0 ],
					"destination" : [ "obj-112", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-83", 0 ],
					"destination" : [ "obj-113", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-81", 0 ],
					"destination" : [ "obj-89", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-52", 0 ],
					"destination" : [ "obj-53", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 554.5, 237.0, 542.0, 237.0, 542.0, 191.0, 554.5, 191.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-53", 0 ],
					"destination" : [ "obj-52", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-52", 1 ],
					"destination" : [ "obj-108", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-22", 0 ],
					"destination" : [ "obj-104", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-97", 0 ],
					"destination" : [ "obj-18", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-13", 0 ],
					"destination" : [ "obj-97", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-64", 0 ],
					"destination" : [ "obj-59", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-61", 0 ],
					"destination" : [ "obj-55", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-57", 0 ],
					"destination" : [ "obj-50", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-47", 0 ],
					"destination" : [ "obj-48", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 44.5, 297.0, 32.0, 297.0, 32.0, 251.0, 44.5, 251.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-48", 0 ],
					"destination" : [ "obj-47", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-43", 0 ],
					"destination" : [ "obj-44", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 44.5, 237.0, 32.0, 237.0, 32.0, 191.0, 44.5, 191.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-44", 0 ],
					"destination" : [ "obj-43", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-35", 0 ],
					"destination" : [ "obj-34", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-33", 0 ],
					"destination" : [ "obj-31", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-12", 0 ],
					"destination" : [ "obj-11", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-11", 0 ],
					"destination" : [ "obj-12", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 389.5, 147.0, 377.0, 147.0, 377.0, 101.0, 389.5, 101.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-9", 0 ],
					"destination" : [ "obj-10", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 254.5, 147.0, 242.0, 147.0, 242.0, 101.0, 254.5, 101.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-10", 0 ],
					"destination" : [ "obj-9", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-1", 0 ],
					"destination" : [ "obj-36", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 254.5, 72.0, 242.0, 72.0, 242.0, 26.0, 254.5, 26.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-36", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-3", 0 ],
					"destination" : [ "obj-2", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-2", 0 ],
					"destination" : [ "obj-3", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 524.5, 147.0, 512.0, 147.0, 512.0, 101.0, 524.5, 101.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-4", 0 ],
					"destination" : [ "obj-5", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 659.5, 147.0, 647.0, 147.0, 647.0, 101.0, 659.5, 101.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-5", 0 ],
					"destination" : [ "obj-4", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 0 ],
					"destination" : [ "obj-8", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-15", 0 ],
					"destination" : [ "obj-22", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-8", 0 ],
					"destination" : [ "obj-15", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-28", 0 ],
					"destination" : [ "obj-27", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-28", 1 ],
					"destination" : [ "obj-30", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-40", 0 ],
					"destination" : [ "obj-41", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 179.5, 237.0, 167.0, 237.0, 167.0, 191.0, 179.5, 191.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-41", 0 ],
					"destination" : [ "obj-40", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-70", 0 ],
					"destination" : [ "obj-71", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 194.5, 432.0, 182.0, 432.0, 182.0, 386.0, 194.5, 386.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-71", 0 ],
					"destination" : [ "obj-70", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-77", 0 ],
					"destination" : [ "obj-76", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-76", 0 ],
					"destination" : [ "obj-77", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 314.5, 432.0, 302.0, 432.0, 302.0, 386.0, 314.5, 386.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-68", 0 ],
					"destination" : [ "obj-69", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 194.5, 492.0, 182.0, 492.0, 182.0, 446.0, 194.5, 446.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-69", 0 ],
					"destination" : [ "obj-68", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-79", 0 ],
					"destination" : [ "obj-80", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 314.5, 492.0, 302.0, 492.0, 302.0, 446.0, 314.5, 446.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-80", 0 ],
					"destination" : [ "obj-79", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-20", 0 ],
					"destination" : [ "obj-96", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-20", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-101", 0 ],
					"destination" : [ "obj-103", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-102", 0 ],
					"destination" : [ "obj-101", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-140", 0 ],
					"destination" : [ "obj-142", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-84", 0 ],
					"destination" : [ "obj-140", 0 ],
					"hidden" : 1,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-66", 0 ],
					"destination" : [ "obj-140", 1 ],
					"hidden" : 1,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-58", 0 ],
					"destination" : [ "obj-57", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-50", 2 ],
					"destination" : [ "obj-58", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 254.5, 320.0, 163.0, 320.0, 163.0, 252.0, 175.5, 252.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-62", 0 ],
					"destination" : [ "obj-61", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-55", 2 ],
					"destination" : [ "obj-62", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 393.5, 320.0, 302.0, 320.0, 302.0, 252.0, 314.5, 252.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-65", 0 ],
					"destination" : [ "obj-64", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-59", 2 ],
					"destination" : [ "obj-65", 0 ],
					"hidden" : 0,
					"color" : [ 0.0, 0.0, 0.0, 1.0 ],
					"midpoints" : [ 764.5, 260.0, 673.0, 260.0, 673.0, 192.0, 685.5, 192.0 ]
				}

			}
 ]
	}

}
