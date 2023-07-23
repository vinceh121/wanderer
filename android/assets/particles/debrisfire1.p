{
	"unique": {
		"billboardBatch": {
			"class": "com.badlogic.gdx.graphics.g3d.particles.ResourceData$SaveData",
			"data": {
				"cfg": {
					"class": "com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch$Config",
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
			"filename": "orig/lib/smoke1/texturenone.ktx",
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
					"continous": true,
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
						"lowMin": 0,
						"lowMax": 0
					},
					"duration": {
						"active": true,
						"lowMin": 3000,
						"lowMax": 3000
					},
					"life": {
						"active": true,
						"lowMin": 0,
						"lowMax": 0,
						"highMin": 1000,
						"highMax": 1000,
						"relative": false,
						"scaling": [
							1
						],
						"timeline": [
							0
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
						"class": "com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer$Single",
						"regions": [
							{
								"u2": 1,
								"v2": 1,
								"halfInvAspectRatio": 0.5
							}
						]
					},
					{
						"class": "com.badlogic.gdx.graphics.g3d.particles.influencers.SpawnInfluencer",
						"spawnShape": {
							"class": "com.badlogic.gdx.graphics.g3d.particles.values.RectangleSpawnShapeValue",
							"active": true,
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
								"active": true,
								"lowMin": 0,
								"lowMax": 0,
								"highMin": 1,
								"highMax": 1,
								"relative": false,
								"scaling": [
									1
								],
								"timeline": [
									0
								]
							},
							"spawnHeightValue": {
								"active": true,
								"lowMin": 0,
								"lowMax": 0,
								"highMin": 1,
								"highMax": 1,
								"relative": false,
								"scaling": [
									1
								],
								"timeline": [
									0
								]
							},
							"spawnDepthValue": {
								"active": true,
								"lowMin": 0,
								"lowMax": 0,
								"highMin": 1,
								"highMax": 1,
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
								"class": "com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier$PolarAcceleration",
								"isGlobal": false,
								"strengthValue": {
									"active": false,
									"lowMin": 0,
									"lowMax": 0,
									"highMin": 5,
									"highMax": 10,
									"relative": false,
									"scaling": [
										1
									],
									"timeline": [
										0
									]
								},
								"thetaValue": {
									"active": false,
									"lowMin": 0,
									"lowMax": 0,
									"highMin": 0,
									"highMax": 360,
									"relative": false,
									"scaling": [
										1
									],
									"timeline": [
										0
									]
								},
								"phiValue": {
									"active": true,
									"lowMin": 0,
									"lowMax": 0,
									"highMin": -35,
									"highMax": 35,
									"relative": false,
									"scaling": [
										1,
										0.43396226,
										0
									],
									"timeline": [
										0,
										0.86986303,
										1
									]
								}
							}
						]
					}
				],
				"renderer": {
					"class": "com.badlogic.gdx.graphics.g3d.particles.renderers.BillboardRenderer"
				}
			}
		]
	}
}
