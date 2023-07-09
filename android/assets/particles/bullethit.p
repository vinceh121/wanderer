{
	"unique": {
		"billboardBatch": {
			"class": "com.badlogic.gdx.graphics.g3d.particles.ResourceData$SaveData",
			"data": {
				"cfg": {
					"class": "com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch$Config",
					"useGPU": true,
					"mode": "Screen"
				}
			},
			"indices": [
				0
			]
		}
	},
	"data": [
	],
	"assets": [
		{
			"filename": "orig/lib/explo18/spritenone.ktx",
			"type": "com.badlogic.gdx.graphics.Texture"
		}
	],
	"resource": {
		"class": "com.badlogic.gdx.graphics.g3d.particles.ParticleEffect",
		"controllers": [
			{
				"name": "Billboard Controller",
				"emitter": {
					"class": "com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter",
					"minParticleCount": 0,
					"maxParticleCount": 200,
					"continous": false,
					"emission": {
						"active": true,
						"lowMin": 0,
						"lowMax": 0,
						"highMin": 250,
						"highMax": 250,
						"relative": false,
						"scaling": [
							1
						],
						"timeline": [
							0
						]
					},
					"delay": {
						"active": false,
						"lowMin": 1000,
						"lowMax": 1000
					},
					"duration": {
						"active": true,
						"lowMin": 500,
						"lowMax": 500
					},
					"life": {
						"active": true,
						"lowMin": 0,
						"lowMax": 0,
						"highMin": 500,
						"highMax": 1000,
						"relative": false,
						"scaling": [
							1,
							1,
							0.3
						],
						"timeline": [
							0,
							0.66,
							1
						]
					},
					"lifeOffset": {
						"active": false,
						"lowMin": 0,
						"lowMax": 0,
						"highMin": 0,
						"highMax": 0,
						"relative": false,
						"scaling": [
							1
						],
						"timeline": [
							0
						]
					}
				},
				"influencers": [
					{
						"class": "com.badlogic.gdx.graphics.g3d.particles.influencers.SpawnInfluencer",
						"spawnShape": {
							"class": "com.badlogic.gdx.graphics.g3d.particles.values.PointSpawnShapeValue",
							"active": false,
							"xOffsetValue": {
								"active": false,
								"lowMin": 0,
								"lowMax": 0
							},
							"yOffsetValue": {
								"active": false,
								"lowMin": 0,
								"lowMax": 0
							},
							"zOffsetValue": {
								"active": false,
								"lowMin": 0,
								"lowMax": 0
							},
							"spawnWidthValue": {
								"active": false,
								"lowMin": 0,
								"lowMax": 0,
								"highMin": 0,
								"highMax": 0,
								"relative": false,
								"scaling": [
									1
								],
								"timeline": [
									0
								]
							},
							"spawnHeightValue": {
								"active": false,
								"lowMin": 0,
								"lowMax": 0,
								"highMin": 0,
								"highMax": 0,
								"relative": false,
								"scaling": [
									1
								],
								"timeline": [
									0
								]
							},
							"spawnDepthValue": {
								"active": false,
								"lowMin": 0,
								"lowMax": 0,
								"highMin": 0,
								"highMax": 0,
								"relative": false,
								"scaling": [
									1
								],
								"timeline": [
									0
								]
							},
							"edges": false
						}
					},
					{
						"class": "com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsInfluencer",
						"velocities": [
							{
								"class": "com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier$BrownianAcceleration",
								"isGlobal": false,
								"strengthValue": {
									"active": false,
									"lowMin": 0,
									"lowMax": 0,
									"highMin": 56,
									"highMax": 56,
									"relative": false,
									"scaling": [
										1,
										0
									],
									"timeline": [
										0,
										0.60273975
									]
								}
							},
							{
								"class": "com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier$PolarAcceleration",
								"isGlobal": false,
								"strengthValue": {
									"active": false,
									"lowMin": 0,
									"lowMax": 0,
									"highMin": 15,
									"highMax": 15,
									"relative": false,
									"scaling": [
										0,
										0,
										1
									],
									"timeline": [
										0,
										0.6849315,
										1
									]
								},
								"thetaValue": {
									"active": false,
									"lowMin": 0,
									"lowMax": 0,
									"highMin": 0,
									"highMax": 0,
									"relative": false,
									"scaling": [
										1
									],
									"timeline": [
										0
									]
								},
								"phiValue": {
									"active": false,
									"lowMin": 0,
									"lowMax": 0,
									"highMin": 180,
									"highMax": 180,
									"relative": false,
									"scaling": [
										1
									],
									"timeline": [
										0
									]
								}
							}
						]
					},
					{
						"class": "com.badlogic.gdx.graphics.g3d.particles.influencers.ScaleInfluencer",
						"value": {
							"active": false,
							"lowMin": 0,
							"lowMax": 0,
							"highMin": 0.5,
							"highMax": 0.5,
							"relative": false,
							"scaling": [
								1
							],
							"timeline": [
								0
							]
						}
					}
				],
				"renderer": {
					"class": "com.badlogic.gdx.graphics.g3d.particles.renderers.BillboardRenderer"
				}
			}
		]
	}
}
